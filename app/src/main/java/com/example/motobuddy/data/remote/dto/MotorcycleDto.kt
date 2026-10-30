package com.example.motobuddy.data.remote.dto

data class MotorcycleDto(
    val id: String? = null,
    val make: String,
    val model: String,
    val year: Int,
    val odometerKm: Int,
    val ownerName: String? = null
)
