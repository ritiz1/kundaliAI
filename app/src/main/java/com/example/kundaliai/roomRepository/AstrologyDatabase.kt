package com.example.kundaliai.roomRepository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kundaliai.roomRepository.birthDetails.User
import com.example.kundaliai.roomRepository.birthDetails.UserDao
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Database(
    entities = [AstrologyReading::class, User::class],
    version = 1,
    exportSchema = false
)
abstract class AstrologyDatabase : RoomDatabase() {
    abstract fun astrologyReadingDao(): AstrologyReadingDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AstrologyDatabase? = null

        fun getDatabase(context: Context): AstrologyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AstrologyDatabase::class.java,
                    "astrology_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

object AstrologyApiClient {

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 15_000
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
    }

    private const val API_URL = "https://json.freeastrologyapi.com/planets"

    suspend fun getNavamsaInfoJsonString(requestData: com.example.kundaliai.astrologyAPIHandle.NavamsaRequest): String? {
        val apiKey = ""

        return try {
            val response = client.post(API_URL) {
                header("x-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(requestData)
            }

            if (response.status == HttpStatusCode.OK) {
                response.body<String>()
            } else {
                println("API Error: ${response.status.description}")
                println("Error content: ${response.body<String>()}")
                null
            }
        } catch (e: Exception) {
            println("Request failed: ${e.message}")
            null
        }
    }
}
