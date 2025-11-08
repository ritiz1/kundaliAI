package com.example.kundaliai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner

//@Composable
//fun LiveSessionScreen(
//    astrologyJsonData: String=""){
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val scope = rememberCoroutineScope()
//    val manager: LiveSessionManager = remember { LiveSessionManager(context,astrologyJsonData)
//}