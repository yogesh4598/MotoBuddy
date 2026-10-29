package com.example.motobuddy.mapping

/**
 * Default implementation when no third-party or open-source mapping engine is active.
 * Provides explicit unconfigured statuses without fabricating live routing or search data.
 */
object UnconfiguredMappingEngine : MapProvider, RouteProvider, PlaceSearchProvider {
    override val providerName: String = "Open-Source Mapping Architecture (Unconfigured)"
    override val isConfigured: Boolean = false

    override fun getStatusMessage(): String {
        return "No active map provider configured. The application is ready to integrate OpenStreetMap, Osmdroid, or OSRM."
    }

    override suspend fun calculateRoute(origin: String, destination: String): RouteResult {
        return RouteResult(
            origin = origin,
            destination = destination,
            distanceKm = 0.0,
            estimatedDurationMinutes = 0.0,
            isRealTimeRouting = false
        )
    }

    override suspend fun searchNearbyPlaces(query: String, location: String?): List<PlaceSearchResult> {
        return emptyList()
    }
}
