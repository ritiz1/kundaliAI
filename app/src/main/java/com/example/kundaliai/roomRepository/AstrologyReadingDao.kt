package com.example.kundaliai.roomRepository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AstrologyReadingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: AstrologyReading)

    // Changed: Returns single object instead of List
    @Query("SELECT * FROM astrology_readings WHERE username = :username")
    suspend fun getReadingByUsername(username: String): AstrologyReading?

    @Query("SELECT * FROM astrology_readings ORDER BY timestamp DESC")
    suspend fun getAllReadings(): List<AstrologyReading>
}
