package com.example.motobuddy.data.remote.dto

data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null
)
