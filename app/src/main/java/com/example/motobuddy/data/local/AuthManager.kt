package com.example.motobuddy.data.local

import android.content.Context

class AuthManager(context: Context) {
    private val prefs = context.getSharedPreferences("motobuddy_auth_prefs", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun saveUserEmail(email: String) {
        prefs.edit().putString("user_email", email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString("user_email", null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNull_or_blank_safe()
    }

    private fun String?.isNull_or_blank_safe(): Boolean {
        return this == null || this.trim().isEmpty()
    }
}
