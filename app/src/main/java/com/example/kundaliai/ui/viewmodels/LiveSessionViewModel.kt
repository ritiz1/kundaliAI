package com.example.kundaliai.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kundaliai.LiveSessionManager
import com.example.kundaliai.roomRepository.AstrologyDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LiveSessionViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AstrologyDatabase.getDatabase(application)
    private val dao = database.astrologyReadingDao()

    private val _astrologyJsonData = MutableStateFlow<String?>(null)
    val astrologyJsonData: StateFlow<String?> = _astrologyJsonData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()
    
    private var liveSessionManager: LiveSessionManager? = null

    fun loadAstrologyData(username: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch the latest reading for this person
                val readings = dao.getReadingsByUsername(username)
                val jsonData = readings.firstOrNull()?.jsonData
                
                if (jsonData != null) {
                    _astrologyJsonData.value = jsonData
                    
                    // Initialize LiveSessionManager with the JSON data
                    liveSessionManager = LiveSessionManager(context, jsonData)
                    
                    _isReady.value = true
                    println("Loaded astrology data for: $username")
                } else {
                    println("No data found for: $username")
                }
            } catch (e: Exception) {
                println("Error loading astrology data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun startListening() {
        viewModelScope.launch {
            liveSessionManager?.startListening(viewModelScope)
        }
    }
    
    fun stopListening() {
        viewModelScope.launch {
            liveSessionManager?.stopListening()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            liveSessionManager?.stopListening()
        }
    }
}