package com.example.kundaliai.roomRepository

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "astrology_readings", indices = [Index(value = ["username"], unique = true)])
data class AstrologyReading(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val jsonData: String,
    val timestamp: Long = System.currentTimeMillis()
)
