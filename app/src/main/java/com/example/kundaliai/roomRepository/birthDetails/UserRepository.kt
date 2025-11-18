package com.example.kundaliai.roomRepository.birthDetails

import android.content.Context
import com.example.kundaliai.roomRepository.AstrologyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(context: Context) {

    private val userDao: UserDao = AstrologyDatabase.getDatabase(context).userDao()

    /**
     * Save a new user to the database
     * @return The userId of the saved user
     */
    suspend fun saveUser(user: User): String = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
        return@withContext user.userId
    }

    /**
     * Get all saved users from the database
     */
    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        return@withContext userDao.getAllUsers()
    }

    /**
     * Get a specific user by their ID
     */
    suspend fun getUserById(userId: String): User? = withContext(Dispatchers.IO) {
        return@withContext userDao.getUserByID(userId)
    }

    /**
     * Check if a user exists by userId
     */
    suspend fun userExists(userId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext userDao.getUserByID(userId) != null
    }

    /**
     * Update an existing user
     */
    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        userDao.insertUser(user) // Room's @Insert with onConflict = REPLACE acts as update
    }

    /**
     * Delete a user by userId
     * NOTE: This won't cascade delete readings - add that logic if needed
     */


    /**
     * Get total count of saved users
     */
    suspend fun getUserCount(): Int = withContext(Dispatchers.IO) {
        return@withContext userDao.getAllUsers().size
    }
}
