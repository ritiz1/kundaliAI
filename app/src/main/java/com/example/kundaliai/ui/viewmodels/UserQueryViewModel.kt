package com.example.kundaliai.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kundaliai.astrologyAPIHandle.AstrologyRepository
import com.example.kundaliai.astrologyAPIHandle.BirthData
import com.example.kundaliai.astrologyAPIHandle.NavamsaRequest
import com.example.kundaliai.roomRepository.AstrologyDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserQueryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AstrologyDatabase.getDatabase(application)
    private val dao = database.astrologyReadingDao()
    private val repository = AstrologyRepository(dao)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _submitSuccess = MutableStateFlow(false)
    val submitSuccess: StateFlow<Boolean> = _submitSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var lastSubmittedName: String = ""

    fun submitBirthData(birthData: BirthData) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Store the name for navigation
                lastSubmittedName = birthData.name.trim()

                // Parse date from "DD/MM/YYYY" format
                val dateParts = birthData.date.split("/")
                val day = dateParts[0].toIntOrNull() ?: 1
                val month = dateParts[1].toIntOrNull() ?: 1
                val year = dateParts[2].toIntOrNull() ?: 2000

                val navamsaRequest = NavamsaRequest(
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
                val success = repository.fetchAndSaveChart(birthData.name, navamsaRequest)

                if (success) {
                    println("Chart saved successfully for: ${birthData.name}")
                    _submitSuccess.value = true
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

    fun getLastSubmittedName(): String = lastSubmittedName

    fun resetSubmitSuccess() {
        _submitSuccess.value = false
    }
}
