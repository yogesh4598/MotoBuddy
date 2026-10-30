package com.example.motobuddy.data.remote.dto

data class ChatRequestDto(
    val message: String,
    val motorcycleModel: String? = null
)

data class ChatResponseDto(
    val reply: String,
    val isWarning: Boolean = false
)
