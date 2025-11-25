package com.example.kundaliai.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kundaliai.roomRepository.AstrologyRepository
import com.example.kundaliai.astrologyAPIHandle.BirthData
import com.example.kundaliai.astrologyAPIHandle.KundliRequest
import com.example.kundaliai.roomRepository.AstrologyDatabase
import com.example.kundaliai.roomRepository.birthDetails.User
import com.example.kundaliai.roomRepository.birthDetails.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class UserQueryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AstrologyDatabase.getDatabase(application)
    private val dao = database.astrologyReadingDao()

    // This is for the user
    private val userRepository= UserRepository(application)
    private val repository = AstrologyRepository(dao)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _submitSuccess = MutableStateFlow(false)
    val submitSuccess: StateFlow<Boolean> = _submitSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _generatedUserId = MutableStateFlow<String?>(null)
    val generatedUserId: StateFlow<String?> = _generatedUserId.asStateFlow()



    fun submitBirthData(birthData: BirthData) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                //Generate unique user ID .
                val userId = UUID.randomUUID().toString()

                val user = User(
                    userId = userId,
                    name = birthData.name,
                    birthDate = birthData.date,
                    birthTime = "${birthData.hours}:${birthData.minutes}:${birthData.seconds}",
                    place = birthData.place
                )

                userRepository.saveUser(user)
                println("User saved: ${user.name} with ID: $userId")

                //------THIS PART IS NOW FOR THE FETCHING AND SAVING OF CHART DATA ----//
                // Parse date from "DD/MM/YYYY" format
                val dateParts = birthData.date.split("/")
                val day = dateParts[0].toIntOrNull() ?: 1
                val month = dateParts[1].toIntOrNull() ?: 1
                val year = dateParts[2].toIntOrNull() ?: 2000

                val navamsaRequest = KundliRequest(
                    year = year,
                    month = month,
                    date = day,
                    hours = birthData.hours,
                    minutes = birthData.minutes,
                    seconds = birthData.seconds,
                    latitude = birthData.latitude,
                    longitude = birthData.longitude,
                    timezone = 5.75 // Nepal timezone
                )

                // Call repository to fetch and save chart
                //userID is the foreign key to identify different users data .


                val success = repository.fetchAndSaveChart(userId, navamsaRequest)

                if (success) {
                    println("Chart saved successfully for: ${birthData.name}")
                    _submitSuccess.value = true
                    _generatedUserId.value = userId
                } else {
                    _errorMessage.value = "Failed to fetch/save chart"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
                println("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun resetSubmitSuccess() {
        _submitSuccess.value = false
    }
    fun clearError(){
        _errorMessage.value = null
    }
}
