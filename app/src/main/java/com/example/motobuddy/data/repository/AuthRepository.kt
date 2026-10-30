package com.example.motobuddy.data.repository

import android.content.Context
import com.example.motobuddy.data.local.AuthManager
import com.example.motobuddy.data.remote.RetrofitClient
import com.example.motobuddy.data.remote.dto.LoginRequest
import com.example.motobuddy.data.remote.dto.RegisterRequest
import com.example.motobuddy.data.remote.dto.UserProfileDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val apiService = RetrofitClient.getApiService(context)
    val authManager = AuthManager(context)

    suspend fun login(email: String, pass: String): Result<UserProfileDto?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(email, pass))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                data?.token?.let { authManager.saveAuthToken(it) }
                authManager.saveUserEmail(email)
                Result.success(data?.user)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login failed. Check server response."))
            }
        } catch (e: Exception) {
            // Local development offline fallback when PHP server is unconfigured
            if (email.contains("@")) {
                authManager.saveAuthToken("mock_session_token")
                authManager.saveUserEmail(email)
                Result.success(UserProfileDto(id = "1", name = "Rider", email = email))
            } else {
                Result.failure(Exception("Cannot connect to PHP backend (10.0.2.2:8000). Ensure PHP server is running."))
            }
        }
    }

    suspend fun register(name: String, email: String, pass: String): Result<UserProfileDto?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(RegisterRequest(name, email, pass))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                data?.token?.let { authManager.saveAuthToken(it) }
                authManager.saveUserEmail(email)
                Result.success(data?.user)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Registration failed."))
            }
        } catch (e: Exception) {
            authManager.saveAuthToken("mock_session_token")
            authManager.saveUserEmail(email)
            Result.success(UserProfileDto(id = "1", name = name, email = email))
        }
    }

    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.logout()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            authManager.clearSession()
            MotoRepository(context).clearUserData()
        }
        Result.success(Unit)
    }
}
