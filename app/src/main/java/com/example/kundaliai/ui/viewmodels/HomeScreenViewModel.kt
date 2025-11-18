package com.example.kundaliai.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kundaliai.roomRepository.birthDetails.User
import com.example.kundaliai.roomRepository.birthDetails.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)

    private val _savedPeople = MutableStateFlow<List<User>>(emptyList())
    val savedPeople: StateFlow<List<User>> = _savedPeople.asStateFlow()

    // ✅ ADD THIS
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSavedUsers()
    }

    private fun loadSavedUsers() {
        viewModelScope.launch {
            _isLoading.value = true  // ✅ Set loading state
            try {
                val users = userRepository.getAllUsers()
                _savedPeople.value = users
                println("✅ Loaded ${users.size} users from database")
            } catch (e: Exception) {
                println("❌ Error loading users: ${e.message}")
                _savedPeople.value = emptyList()
            } finally {
                _isLoading.value = false  // ✅ Reset loading state
            }
        }
    }

    // ✅ ADD THIS METHOD
    fun refreshUsers() {
        loadSavedUsers()
    }

    suspend fun getUserById(userId: String): User? {
        return userRepository.getUserById(userId)
    }
}
