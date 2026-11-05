package com.example.motobuddy.data.remote.dto

data class FuelEntryDto(
    val id: String? = null,
    val date: String = "",
    val odometerKm: Int = 0,
    val liters: Double = 0.0,
    val pricePerLiter: Double = 0.0,
    val totalCost: Double = 0.0,
    val notes: String? = ""
)
