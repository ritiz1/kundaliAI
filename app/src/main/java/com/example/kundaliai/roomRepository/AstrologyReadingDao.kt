package com.example.kundaliai.roomRepository


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AstrologyReadingDao {
    // Replace existing row when username (or primary key) conflicts to prevent duplicates
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: AstrologyReading)

    @Query("SELECT * FROM astrology_readings WHERE username = :username")
    suspend fun getReadingsByUsername(username: String): List<AstrologyReading>

    @Query("SELECT * FROM astrology_readings ORDER BY timestamp DESC")
    suspend fun getAllReadings(): List<AstrologyReading>
}
