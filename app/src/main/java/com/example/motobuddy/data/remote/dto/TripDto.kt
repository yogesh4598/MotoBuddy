package com.example.motobuddy.data.remote.dto

data class TripDto(
    val id: String? = null,
    val title: String,
    val startLocation: String,
    val destination: String,
    val distanceKm: Double,
    val estDurationHours: Double,
    val fuelCostEst: Double,
    val date: String,
    val notes: String? = null
)
