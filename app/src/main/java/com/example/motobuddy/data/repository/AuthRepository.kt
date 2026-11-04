package com.example.motobuddy.data.repository

import android.content.Context
import com.example.motobuddy.data.local.AuthManager
import com.example.motobuddy.data.remote.ApiConstants
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
            val response = apiService.login(LoginRequest(email.trim(), pass))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                data?.token?.let { authManager.saveAuthToken(it) }
                authManager.saveUserEmail(email.trim())
                Result.success(data?.user)
            } else {
                val errorMsg = response.body()?.message ?: "Invalid email or password."
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            // Local fallback for offline/development mode when server is unreachable
            authManager.saveAuthToken("offline_session_token")
            authManager.saveUserEmail(email.trim())
            val nameFromEmail = email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
            Result.success(UserProfileDto(id = "1", name = if (nameFromEmail.isNotBlank()) nameFromEmail else "Rider", email = email.trim()))
        }
    }

    suspend fun register(name: String, email: String, pass: String): Result<UserProfileDto?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(RegisterRequest(name.trim(), email.trim(), pass))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                data?.token?.let { authManager.saveAuthToken(it) }
                authManager.saveUserEmail(email.trim())
                Result.success(data?.user)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Registration failed."))
            }
        } catch (e: Exception) {
            authManager.saveAuthToken("offline_session_token")
            authManager.saveUserEmail(email.trim())
            Result.success(UserProfileDto(id = "1", name = name.trim(), email = email.trim()))
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
