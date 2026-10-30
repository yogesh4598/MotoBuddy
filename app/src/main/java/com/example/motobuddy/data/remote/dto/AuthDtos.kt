package com.example.motobuddy.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponseData(
    val token: String?,
    val user: UserProfileDto?
)

data class UserProfileDto(
    val id: String?,
    val name: String?,
    val email: String?
)
