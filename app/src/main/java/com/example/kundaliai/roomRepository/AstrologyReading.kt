package com.example.kundaliai.roomRepository

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "astrology_readings", indices = [Index(value = ["username"], unique = true)])
data class AstrologyReading(
    @PrimaryKey
    val username: String,
    val d1Data: String,
    val d9Data: String="",
    val dashaData: String="",
    val timestamp: Long = System.currentTimeMillis()
)
