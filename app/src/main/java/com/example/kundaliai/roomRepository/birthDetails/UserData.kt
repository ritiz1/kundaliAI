package com.example.kundaliai.roomRepository.birthDetails

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val userId :String , // Using UUDI as primary key
    val name : String ,
    val birthDate: String ,
    val birthTime : String ,
    val place: String,
    val createdAt: Long = System.currentTimeMillis()
)