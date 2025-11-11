package com.example.kundaliai.astrologyAPIHandle

import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json



// We use an "object" here so we only ever have
// one instance of this client (a "Singleton").
object AstrologyApiClient {

    // 1. Create the Ktor client
    private val client = HttpClient(OkHttp) {
        // Timeouts (helps avoid hanging on poorly connected networks)
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 15_000
        }

        // JSON serialization / deserialization
        install(ContentNegotiation) {
            json(Json {
                // These are just helpful settings
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
    }

    // The API's address
    private const val API_URL = "https://json.freeastrologyapi.com/planets"

    /**
     * Calls the astrology API and returns the raw JSON string.
     * This is a "suspend" function, so it must be called
     * from a coroutine (like in 'runBlocking' or 'viewModelScope').
     *
     * @param requestData The NavamsaRequest object with all the birth details.
     * @return The raw JSON String on success, or null on failure.
     */
    suspend fun getNavamsaInfoJsonString(requestData: NavamsaRequest): String? {

        // --- !!! IMPORTANT !!! ---
        // Replace this with your actual API key
        val apiKey = "jTqNwH0WKK2RSlE86jycwJhBVXrOz4c37W849m6c"
        // -------------------------

        try {
            // This is where the network call happens
            val response = client.post(API_URL) {
                // Set the "x-api-key" header
                header("x-api-key", apiKey)

                // Tell the server we are sending JSON
                contentType(ContentType.Application.Json)

                // Put our NavamsaRequest object in the body
                // Ktor will automatically turn it into a JSON string
                setBody(requestData)
            }

            // Check if the server said "OK" (code 200)
            if (response.status == HttpStatusCode.OK) {
                // Success! Return the raw response body as a String.
                return response.body<String>()
            } else {
                // The server gave us an error (like 401, 403, 500)
                println("API Error: ${response.status.description}")
                println("Error content: ${response.body<String>()}")
                return null
            }
        } catch (e: Exception) {
            // A network error happened (no internet, timeout, etc.)
            println("Request failed: ${e.message}")
            return null
        }
    }
}