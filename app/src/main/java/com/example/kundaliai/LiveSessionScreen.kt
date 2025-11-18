package com.example.kundaliai

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kundaliai.ui.theme.KundaliOrangePrimary
import com.example.kundaliai.ui.theme.KundaliTextWhite
import com.example.kundaliai.ui.viewmodels.LiveSessionViewModel

@Composable
fun LiveSessionScreen(username: String) {
    val context = LocalContext.current
    val viewModel: LiveSessionViewModel = viewModel()

    val isLoading by viewModel.isLoading.collectAsState()
    val isReady by viewModel.isReady.collectAsState()

    var hasAudioPermission by remember { mutableStateOf(false) }

    // Check audio permission
    LaunchedEffect(Unit) {
        hasAudioPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    val requestAudioPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
        if (granted && isReady) {
            viewModel.startListening()
        }
    }

    // Load astrology data when screen opens
    LaunchedEffect(username) {
        viewModel.loadAstrologyData(username, context)
    }

    // Start listening when ready and has permission
    LaunchedEffect(isReady, hasAudioPermission) {
        if (isReady && hasAudioPermission) {
            viewModel.startListening()
        }
    }

    // Stop listening when screen closes
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopListening()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(color = KundaliOrangePrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading your Kundali data...",
                    fontSize = 16.sp,
                    color = KundaliTextWhite
                )
            }

            !hasAudioPermission -> {
                Text(
                    text = "🎤",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Microphone Permission Required",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = KundaliTextWhite
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "We need microphone access to have a voice conversation about your Kundali.",
                    fontSize = 14.sp,
                    color = KundaliTextWhite.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { requestAudioPermission.launch(Manifest.permission.RECORD_AUDIO) },
                    colors = ButtonDefaults.buttonColors(containerColor = KundaliOrangePrimary)
                ) {
                    Text("Grant Permission")
                }
            }

            isReady -> {
                Text(
                    text = "🎙️",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Live Session Active!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = KundaliTextWhite
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Kundali AI is listening...",
                    fontSize = 16.sp,
                    color = KundaliOrangePrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ask me anything about your birth chart!",
                    fontSize = 14.sp,
                    color = KundaliOrangePrimary.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.stopListening() },
                    colors = ButtonDefaults.buttonColors(containerColor = KundaliOrangePrimary)
                ) {
                    Text("End Session")
                }
            }

            else -> {
                Text(
                    text = "❌",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "No Kundali Data Found",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = KundaliTextWhite
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Please generate your Kundali first.",
                    fontSize = 14.sp,
                    color = KundaliTextWhite.copy(alpha = 0.7f)
                )
            }
        }
    }
}

