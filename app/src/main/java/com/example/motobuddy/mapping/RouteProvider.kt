package com.example.motobuddy.mapping

data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val label: String
)

data class RouteResult(
    val origin: String,
    val destination: String,
    val distanceKm: Double,
    val estimatedDurationMinutes: Double,
    val isRealTimeRouting: Boolean,
    val points: List<RoutePoint> = emptyList()
)

/**
 * Interface representing a replaceable Routing Provider
 * (e.g., OSRM, GraphHopper, OpenRouteService).
 */
interface RouteProvider {
    suspend fun calculateRoute(origin: String, destination: String): RouteResult
}
