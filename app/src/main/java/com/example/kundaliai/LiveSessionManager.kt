// kotlin
package com.example.kundaliai

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.LiveSession
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.SpeechConfig
import com.google.firebase.ai.type.Voice
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.liveGenerationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(PublicPreviewAPI::class)
class LiveSessionManager(
    private val context: Context,
    private val d1Data:String? ,
    private val d9Data: String? ,
    private val dashaData: String? // Add JSON data parameter
) {

    private var job: Job? = null
    var session: LiveSession? = null
        private set
    private var isListening = false
    private var sessionConnected = false
    private var reconnectAttempts = 0
    private val MAX_RECONNECT_ATTEMPTS = 5
    private val RECONNECT_DELAY_MS = 2000L

    private var currentVoice = "Zephyr"

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null


    // Keep track of previous audio routing state so we can restore it on cleanup
    private var previousAudioMode: Int? = null
    private var previousSpeakerphoneOn: Boolean? = null
    private val language= "Nepali"

    // Reusable listener for pre-S audio focus APIs
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        Log.d("GEMINI_LIVE", ">>> Audio focus changed: $focusChange")
        // Handle focus changes if you want to pause/resume listening or playback
        // e.g. if (focusChange == AudioManager.AUDIOFOCUS_LOSS) stopListening()
    }

    //---------------------------------------THE MODEL IS HERE ----------------------------------------------------

    private val model by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
            modelName = "gemini-live-2.5-flash-preview",
            generationConfig = liveGenerationConfig {
                responseModality = ResponseModality.AUDIO
                speechConfig = SpeechConfig(voice = Voice(currentVoice))
            },
            systemInstruction = content {
                text("""
                    ROLE:
                        You are "The Cosmic Auditor." You are an advanced Vedic Astrology AI. Your goal is to diagnose the user's life based on their chart using strict logic and cause-and-effect.

                        *** CRITICAL INSTRUCTION: LANGUAGE ***
                        You must respond entirely in $language. Don't use complicated words- keep it simple and direct. Use everyday $language that a common person would understand.
                        Even though you are speaking $language,  Do not become overly poetic just because of the language change.

                        CRITICAL RULE: THE "PING-PONG" PROTOCOL
                        - MAXIMUM LENGTH: You are FORBIDDEN from speaking more than a minute.
                        - NO LECTURES: Do not dump the whole analysis at once. Feed it in small, addictive bites.
                        - ALWAYS ASK: Every single response must end with a direct, provocative question in $language related to the insight you just gave. You must force the user to confirm your analysis before moving to the next point.

                        TONE:
                        - Brutal but grounded.
                        - Concise.
                        - Punchy.

                        INTERACTION FLOW:
                        1. Analyze ONE specific conflict or placement (e.g., Moon in 6th House).
                        2. Explain the immediate consequence in 1-2 sentences in $language.
                        3. Ask a question in $language to verify the experience.
                        4. Wait for user input before analyzing the next placement.

                        START with saying their rashis and lagna. 
//                       
                        GOAL:
                        Keep the user addicted to the conversation by making it feel like a live, rapid-fire consultation.
               
                        The users birth chart(D1 char) is $d1Data.  
                        The users Navamsa chart(D9 chart) is $d9Data.
                        The users Dasha periods are $dashaData.
                """.trimIndent())
            }
        )
    }
    //---------------------------------------THE MODEL IS HERE ----------------------------------------------------



    fun startListening(scope: CoroutineScope) {
        Log.d("GEMINI_LIVE", ">>> startListening called")

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("GEMINI_LIVE", ">>> RECORD_AUDIO permission not granted!")
            return
        }

        if (job?.isActive == true) {
            Log.w("GEMINI_LIVE", ">>> Job already active, skipping")
            return
        }

        isListening = true
        sessionConnected = false
        reconnectAttempts = 0
        job = scope.launch(Dispatchers.IO) {
            while (isListening) {
                try {
                    Log.d("GEMINI_LIVE", ">>> Connecting to model (attempt ${reconnectAttempts + 1})...")
                    session = model.connect()
                    sessionConnected = true
                    reconnectAttempts = 0
                    Log.d("GEMINI_LIVE", ">>> Connected! Starting audio conversation...")

                    requestAudioFocus()

                    Log.d("GEMINI_LIVE", ">>> Starting audio conversation (SDK handles all recording/responding/playback)...")
                    session?.startAudioConversation()
                    Log.d("GEMINI_LIVE", ">>> Audio conversation is running! Waiting for it to complete...")

                    // Keep monitoring the conversation
                    while (isListening && sessionConnected) {
                        delay(1000)

                        try {
                            if (session?.isAudioConversationActive() != true) {
                                Log.d("GEMINI_LIVE", ">>> Conversation ended, will reconnect in ${RECONNECT_DELAY_MS}ms...")
                                sessionConnected = false
                                break
                            }
                        } catch (e: Exception) {
                            Log.e("GEMINI_LIVE", ">>> Error checking conversation status: ${e.message}")
                            sessionConnected = false
                            break
                        }
                    }

                    // If user wants to keep listening, attempt reconnect
                    if (isListening) {
                        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                            reconnectAttempts++
                            Log.d("GEMINI_LIVE", ">>> Reconnecting... (attempt $reconnectAttempts/$MAX_RECONNECT_ATTEMPTS)")
                            cleanupSession()
                            delay(RECONNECT_DELAY_MS)
                            // Loop will reconnect
                        } else {
                            Log.e("GEMINI_LIVE", ">>> Max reconnection attempts reached. Stopping.")
                            isListening = false
                            break
                        }
                    }

                } catch (e: Exception) {
                    Log.e("GEMINI_LIVE", ">>> Connection/conversation error: ${e.message}", e)
                    sessionConnected = false

                    if (isListening && reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                        reconnectAttempts++
                        Log.d("GEMINI_LIVE", ">>> Error occurred, reconnecting... (attempt $reconnectAttempts/$MAX_RECONNECT_ATTEMPTS)")
                        cleanupSession()
                        delay(RECONNECT_DELAY_MS)
                    } else {
                        Log.e("GEMINI_LIVE", ">>> Cannot reconnect, stopping.")
                        isListening = false
                        break
                    }
                }
            }

            Log.d("GEMINI_LIVE", ">>> Exiting listen loop")
            cleanup()
        }
    }

    private suspend fun cleanupSession() {
        Log.d("GEMINI_LIVE", ">>> Cleaning up session...")
        try {
            session?.stopAudioConversation()
        } catch (e: Exception) {
            Log.w("GEMINI_LIVE", ">>> Error stopping conversation: ${e.message}")
        }

        try {
            session?.close()
        } catch (e: Exception) {
            Log.w("GEMINI_LIVE", ">>> Error closing session: ${e.message}")
        }

        session = null
        abandonAudioFocus()
    }

    private suspend fun cleanup() {
        Log.d("GEMINI_LIVE", ">>> Starting cleanup...")
        try {
            session?.stopAudioConversation()
            Log.d("GEMINI_LIVE", ">>> Audio conversation stopped")
        } catch (e: Exception) {
            Log.w("GEMINI_LIVE", ">>> Error stopping conversation: ${e.message}")
        }

        try {
            session?.close()
            Log.d("GEMINI_LIVE", ">>> Session closed")
        } catch (e: Exception) {
            Log.w("GEMINI_LIVE", ">>> Error closing session: ${e.message}")
        }

        abandonAudioFocus()

        session = null
        isListening = false
        Log.d("GEMINI_LIVE", ">>> Cleanup complete")
    }

    suspend fun stopListening() {
        Log.d("GEMINI_LIVE", ">>> stopListening called")
        isListening = false
        job?.cancelAndJoin()
        cleanup()
    }

    private fun requestAudioFocus() {
        Log.d("GEMINI_LIVE", ">>> Requesting audio focus...")
        val attr = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        // Capture previous routing/mode so we can restore after we're done
        try {
            previousAudioMode = audioManager.mode
            previousSpeakerphoneOn = audioManager.isSpeakerphoneOn
        } catch (e: Exception) {
            Log.w("GEMINI_LIVE", ">>> Could not capture previous audio state: ${e.message}")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Use AudioFocusRequest on newer Android versions
            try {
                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                    .setAudioAttributes(attr)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .build()

                val result = audioFocusRequest?.let { audioManager.requestAudioFocus(it) }
                Log.d("GEMINI_LIVE", ">>> Audio focus request result: $result")
                if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    Log.w("GEMINI_LIVE", ">>> Audio focus not granted (S+): $result")
                }
            } catch (e: Exception) {
                Log.w("GEMINI_LIVE", ">>> Failed to request audio focus (S+): ${e.message}")
            }
        } else {
            // Legacy request for pre-S devices
            try {
                val result = audioManager.requestAudioFocus(
                    audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                )
                Log.d("GEMINI_LIVE", ">>> Audio focus request result: $result")
                if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    Log.w("GEMINI_LIVE", ">>> Audio focus not granted (pre-S): $result")
                }
            } catch (e: Exception) {
                Log.w("GEMINI_LIVE", ">>> Failed to request audio focus (pre-S): ${e.message}")
            }
        }

        // Ensure audio goes to speaker
        try {
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.isSpeakerphoneOn = true
            Log.d("GEMINI_LIVE", ">>> Audio routed to speaker, speakerphone: ${audioManager.isSpeakerphoneOn}")
        } catch (e: Exception) {
            Log.w("GEMINI_LIVE", ">>> Failed to route audio to speaker: ${e.message}")
        }
    }

    private fun abandonAudioFocus() {
        Log.d("GEMINI_LIVE", ">>> Abandoning audio focus...")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            } else {
                audioManager.abandonAudioFocus(audioFocusChangeListener)
            }
        } catch (e: Exception) {
            Log.w("GEMINI_LIVE", ">>> Error abandoning audio focus: ${e.message}")
        }

        // Restore previous routing/mode if we captured it
        try {
            previousAudioMode?.let { audioManager.mode = it }
            previousSpeakerphoneOn?.let { audioManager.isSpeakerphoneOn = it }
            Log.d("GEMINI_LIVE", ">>> Restored previous audio mode and speakerphone state")
        } catch (e: Exception) {
            Log.w("GEMINI_LIVE", ">>> Failed to restore audio state: ${e.message}")
        }

        // Clear focus request and previous state to avoid stale values
        audioFocusRequest = null
        previousAudioMode = null
        previousSpeakerphoneOn = null
    }
}
