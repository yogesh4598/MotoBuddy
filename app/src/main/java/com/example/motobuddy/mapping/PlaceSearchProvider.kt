package com.example.motobuddy.mapping

data class PlaceSearchResult(
    val id: String,
    val name: String,
    val address: String,
    val category: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distanceKm: Double? = null
)

/**
 * Interface representing a replaceable Place Search Provider
 * (e.g., Nominatim, Overpass API, Photon).
 */
interface PlaceSearchProvider {
    suspend fun searchNearbyPlaces(query: String, location: String? = null): List<PlaceSearchResult>
}
