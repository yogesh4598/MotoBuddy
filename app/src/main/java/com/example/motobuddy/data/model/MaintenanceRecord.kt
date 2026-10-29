package com.example.motobuddy.data.model

import java.util.UUID

data class MaintenanceRecord(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String, // e.g. "Engine Oil", "Chain Lube", "Brakes", "Tyres"
    val date: String,
    val odometerKm: Int,
    val notes: String = "",
    val isCompleted: Boolean = true
)
