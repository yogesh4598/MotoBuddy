package com.example.motobuddy.data.remote.dto

data class MaintenanceDto(
    val id: String? = null,
    val title: String,
    val category: String,
    val date: String,
    val odometerKm: Int,
    val notes: String? = null,
    val isCompleted: Boolean = true
)
