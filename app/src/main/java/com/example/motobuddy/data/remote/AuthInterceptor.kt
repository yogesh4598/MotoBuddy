package com.example.motobuddy.data.remote

import com.example.motobuddy.data.local.AuthManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authManager: AuthManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = authManager.getAuthToken()

        val requestBuilder = original.newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        requestBuilder.header("Accept", "application/json")

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
