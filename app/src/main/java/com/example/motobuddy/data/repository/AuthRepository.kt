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
import org.json.JSONObject
import retrofit2.Response

class AuthRepository(private val context: Context) {
    private val apiService = RetrofitClient.getApiService(context)
    val authManager = AuthManager(context)

    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorJson = response.errorBody()?.string()
            if (!errorJson.isNullOrBlank() && errorJson.trim().startsWith("{")) {
                val jsonObject = JSONObject(errorJson)
                jsonObject.optString("message", "Server error (${response.code()})")
            } else {
                "Server error (${response.code()}): ${response.message()}"
            }
        } catch (e: Exception) {
            "Server error (${response.code()}): ${response.message()}"
        }
    }

    suspend fun login(email: String, pass: String): Result<UserProfileDto?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(email.trim(), pass))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val data = body.data
                    data?.token?.let { authManager.saveAuthToken(it) }
                    authManager.saveUserEmail(email.trim())
                    Result.success(data?.user)
                } else {
                    Result.failure(Exception(body?.message ?: "Invalid email or password."))
                }
            } else {
                val errorMsg = parseErrorMessage(response)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Cannot connect to server at ${ApiConstants.BASE_URL}. Ensure XAMPP is running."))
        }
    }

    suspend fun register(name: String, email: String, pass: String): Result<UserProfileDto?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(RegisterRequest(name.trim(), email.trim(), pass))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val data = body.data
                    data?.token?.let { authManager.saveAuthToken(it) }
                    authManager.saveUserEmail(email.trim())
                    Result.success(data?.user)
                } else {
                    Result.failure(Exception(body?.message ?: "Registration failed."))
                }
            } else {
                val errorMsg = parseErrorMessage(response)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Cannot connect to server at ${ApiConstants.BASE_URL}. Ensure XAMPP is required."))
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
