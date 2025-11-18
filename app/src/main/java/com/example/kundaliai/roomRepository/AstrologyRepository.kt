package com.example.kundaliai.roomRepository

import com.example.kundaliai.astrologyAPIHandle.NavamsaRequest

class AstrologyRepository(private val dao: AstrologyReadingDao) {

    suspend fun fetchAndSaveChart(
        username: String,
        navamsaRequest: NavamsaRequest
    ): Boolean {
        return try {
            val jsonResponse = AstrologyApiClient.getNavamsaInfoJsonString(navamsaRequest)

            if (jsonResponse != null) {
                val reading = AstrologyReading(
                    username = username,
                    jsonData = jsonResponse
                )
                dao.insertReading(reading)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("Repository error: ${e.message}")
            false
        }
    }
}