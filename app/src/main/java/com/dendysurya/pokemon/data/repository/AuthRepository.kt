package com.dendysurya.pokemon.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dendysurya.pokemon.data.local.dao.UserDao
import com.dendysurya.pokemon.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.IOException


private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class AuthRepository(
    private val context: Context,
    private val userDao: UserDao
) {

    private val dataStore = context.userPreferencesDataStore


    // Keys for DataStore
    private val EMAIL_KEY = stringPreferencesKey("email")
    private val TOKEN_KEY = stringPreferencesKey("token")

    // Flow to observe authentication state
    val isAuthenticated: Flow<Boolean> = dataStore.data
        .map { preferences ->
            val email = preferences[EMAIL_KEY]
            val token = preferences[TOKEN_KEY]
            !email.isNullOrEmpty() && !token.isNullOrEmpty()
        }

    // Get current user if authenticated
    suspend fun getCurrentUser(): UserData? {
        val preferences = dataStore.data.first()
        val email = preferences[EMAIL_KEY] ?: return null
        val token = preferences[TOKEN_KEY] ?: return null

        return userDao.getUserByCredentials(email, token)
    }

    // Login function
    suspend fun login(email: String, password: String): Result<UserData> {
        return try {
            // In a real app, you'd hash the password
            // For this example, we'll use password as token
            val token = password.hashCode().toString()
            val user = userDao.getUserByEmail(email)

            if (user != null && user.token == token) {
                // Save auth data to DataStore
                dataStore.edit { preferences ->
                    preferences[EMAIL_KEY] = email
                    preferences[TOKEN_KEY] = token
                }
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Register function
    suspend fun register(username: String, email: String, password: String): Result<UserData> {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                return Result.failure(Exception("Email already registered"))
            }

            // In a real app, you'd hash the password
            val token = password.hashCode().toString()
            val newUser = UserData(
                email = email,
                name = username,
                token = token
            )

            val userId = userDao.insertUser(newUser)
            if (userId > 0) {
                Result.success(newUser)
            } else {
                Result.failure(Exception("Failed to register user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Logout function
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(EMAIL_KEY)
            preferences.remove(TOKEN_KEY)
        }
    }
}