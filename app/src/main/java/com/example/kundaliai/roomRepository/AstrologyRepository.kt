package com.example.kundaliai.roomRepository

import com.example.kundaliai.astrologyAPIHandle.KundliRequest

class AstrologyRepository(private val dao: AstrologyReadingDao) {

    suspend fun fetchAndSaveChart(
        username: String,
        kundliRequest: KundliRequest
    ): Boolean {
        return try {

            //Fetch D1 data
            val d1Data = AstrologyApiClient.getAstrologyData(
                AstrologyApiClient.Endpoints.PLANETS,
                kundliRequest
            )

            val d9Data = AstrologyApiClient.getAstrologyData(
                AstrologyApiClient.Endpoints.D9_DATA,
                kundliRequest
            )

            val dashaData = AstrologyApiClient.getAstrologyData(
                AstrologyApiClient.Endpoints.DASHA_DATA,
                kundliRequest
            )

            if (d1Data != null && d9Data != null && dashaData != null) {
                val reading = AstrologyReading(
                    username = username,
                    d1Data = d1Data,
                    d9Data = d9Data,
                    dashaData = dashaData
                )
                dao.insertReading(reading)
                true
            } else {
                println("One or more API responses were null")
                false
            }
        } catch (e: Exception) {
            println("Repository error: ${e.message}")
            false
        }
    }
    }