package com.example.kundaliai.astrologyAPIHandle

import kotlinx.serialization.Serializable

@Serializable
data class NavamsaRequest(
    val year: Int,
    val month: Int,
    val date: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val latitude: Double,
    val longitude: Double,
    val timezone: Double
)
