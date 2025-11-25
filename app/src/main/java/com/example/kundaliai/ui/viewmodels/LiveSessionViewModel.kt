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

    //Seperate StateLows for each data types
    private val _d1Data = MutableStateFlow<String?>(null)
    val d1Data: StateFlow<String?> = _d1Data.asStateFlow()

    private val _d9Data = MutableStateFlow<String?>(null)
    val d9Data: StateFlow<String?> = _d9Data.asStateFlow()

    private val _dashaData = MutableStateFlow<String?>(null)
    val dashaData: StateFlow<String?> = _dashaData.asStateFlow()


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
                val reading = dao.getReadingByUsername(username)
                if(reading!= null){
                    //I have three data fields d1Data,d9Data,d10Data
                    _d1Data.value = reading.d1Data
                    _d9Data.value = reading.d9Data
                    _dashaData.value = reading.dashaData

                    //Initialize LiveSessionManager with ALL three data fields
                    liveSessionManager = LiveSessionManager(
                        context=context,
                        d1Data=reading.d1Data,
                        d9Data=reading.d9Data,
                        dashaData=reading.dashaData
                    )
                    _isReady.value = true

                    println("Loaded astrology data for: $username")
                    println("D1 Data: ${reading.d1Data.take(100)}...") // Show first 100 chars
                    println("D9 Data: ${reading.d9Data.take(100)}...")
                    println("Dasha Data: ${reading.dashaData.take(100)}...")
                } else {
                    println("No data found for: $username")
                    _isReady.value = false

                }
            } catch (e: Exception) {
                println("Error loading astrology data: ${e.message}")
                _isReady.value = false
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