package com.example.motobuddy.data.model

import java.util.UUID

data class FuelEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: String = "",
    val odometerKm: Int = 0,
    val liters: Double = 0.0,
    val pricePerLiter: Double = 0.0,
    val totalCost: Double = 0.0,
    val notes: String = ""
)
