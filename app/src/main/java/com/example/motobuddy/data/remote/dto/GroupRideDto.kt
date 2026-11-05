package com.example.motobuddy.data.remote.dto

data class GroupRideDto(
    val id: String? = null,
    val title: String = "",
    val routeDescription: String = "",
    val meetupLocation: String = "",
    val meetupTime: String = "",
    val maxRiders: Int = 8,
    val hostName: String? = "Rider"
)
