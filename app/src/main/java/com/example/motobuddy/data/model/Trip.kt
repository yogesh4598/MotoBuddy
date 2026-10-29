package com.example.motobuddy.data.model

import java.util.UUID

data class Trip(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val startLocation: String,
    val destination: String,
    val distanceKm: Double,
    val estDurationHours: Double,
    val fuelCostEst: Double,
    val date: String,
    val notes: String = ""
)
