package com.example.kundaliai.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

// Primary Background Colors
val DeepSlate = Color(0xFF0F172A)        // slate-900
val DeepMaroon = Color(0xFF450A0A)       // red-950
val DeepPurple = Color(0xFF1E1B4B)       // purple-950

// Accent Gold Colors
val BrightGold = Color(0xFFFACC15)       // yellow-400
val RichGold = Color(0xFFEAB308)         // yellow-500
val SoftAmber = Color(0xFFFBBF24)        // amber-400
val DeepAmber = Color(0xFFF59E0B)        // amber-500
val DarkAmber = Color(0xFFD97706)        // amber-600

// Primary Maroon/Red Colors
val Maroon = Color(0xFFB91C1C)           // red-700
val DarkMaroon = Color(0xFF7F1D1D)       // red-900
val MediumMaroon = Color(0xFF991B1B)     // red-800

// Text Colors
val LightText = Color(0xFFF8FAFC)        // slate-50
val OffWhite = Color(0xFFF1F5F9)         // slate-100
val LightGray = Color(0xFFE2E8F0)        // slate-200
val MediumGray = Color(0xFFCBD5E1)       // slate-300
val DarkGray = Color(0xFF94A3B8)         // slate-400

// Supporting Colors
val DarkSlate = Color(0xFF1E293B)        // slate-800
val OrangeTone = Color(0xFFEA580C)      // orange-600

// Helper: blend two colors into one (t = 0f -> a, t = 1f -> b)
fun blend(colorA: Color, colorB: Color, t: Float): Color =
    lerp(colorA, colorB, t.coerceIn(0f, 1f))

// Prebuilt Brushes (use in Modifier.background or Button backgrounds)
val BackgroundGradient = Brush.linearGradient(
    colors = listOf(DeepSlate, DeepPurple, DeepMaroon)
)

val PrimaryButtonGradient = Brush.horizontalGradient(
    colors = listOf(Maroon, DarkMaroon)
)

val AccentGoldGradient = Brush.linearGradient(
    colors = listOf(BrightGold, RichGold)
)
// Kundali Form Colors
val KundaliDarkBackground = Color(0xFF27150C)
val KundaliOrangePrimary = Color(0xFFE47010)
val KundaliTextLight = Color(0xFFFFCC80)
val KundaliTextWhite = Color(0xFFFFFFFF)
val KundaliInputBackground = Color(0xFF502717)
// Opacity / alpha suggestions (use .copy(alpha = x))
// e.g. RichGold.copy(alpha = 0.3f), BrightGold.copy(alpha = 0.2f)
