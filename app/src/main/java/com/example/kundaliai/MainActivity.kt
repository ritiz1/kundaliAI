// kotlin
package com.example.kundaliai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.kundaliai.astrologyAPIHandle.AstrologyRepository
import com.example.kundaliai.astrologyAPIHandle.NavamsaRequest
import com.example.kundaliai.roomRepository.AstrologyDatabase
import com.example.kundaliai.roomRepository.AstrologyReading
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            UserQueryForm(
                onSubmit = { birthData ->
                    scope.launch {
                        val database = AstrologyDatabase.getDatabase(context)
                        val dao = database.astrologyReadingDao()

                        // Parse date from "DD/MM/YYYY" format
                        val dateParts = birthData.date.split("/")
                        val day = dateParts[0].toIntOrNull() ?: 1
                        val month = dateParts[1].toIntOrNull() ?: 1
                        val year = dateParts[2].toIntOrNull() ?: 2000

                        val navamsaRequest = NavamsaRequest(
                            year = year,
                            month = month,
                            date = day,
                            hours = birthData.hours,
                            minutes = birthData.minutes,
                            seconds = birthData.seconds,
                            latitude = birthData.latitude,
                            longitude = birthData.longitude,
                            timezone = 5.5 // IST timezone
                        )

                        // Call repository to fetch and save chart
                        val repository = AstrologyRepository(dao)
                        val success = repository.fetchAndSaveChart(birthData.name, navamsaRequest)

                        if (success) {
                            println("Chart saved successfully for: ${birthData.name}")
                        } else {
                            println("Failed to fetch/save chart")
                        }
                    }
                }
            )

                    }
                }

        }

//                LiveSessionScreen()






@Composable
fun LiveSessionScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val manager: LiveSessionManager = remember { LiveSessionManager(context) }

    var listening by remember { mutableStateOf(false) }

    val requestAudioPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && !listening) {
            listening = true
            manager.startListening(scope)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                scope.launch {
                    manager.stopListening()
                    listening = false
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    fun ensurePermissionAndStart() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            if (!listening) {
                listening = true
                manager.startListening(scope)
            }
        } else {
            requestAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (listening) "Status: Listening…" else "Status: Idle",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))
            Button(
                enabled = !listening,
                onClick = { ensurePermissionAndStart() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Start Talking") }
            Spacer(Modifier.height(12.dp))
            Button(
                enabled = listening,
                onClick = {
                    scope.launch {
                        manager.stopListening()
                        listening = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Stop") }
        }
    }
}

