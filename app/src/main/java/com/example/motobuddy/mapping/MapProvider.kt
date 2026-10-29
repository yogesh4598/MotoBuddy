package com.example.motobuddy.mapping

/**
 * Interface representing a replaceable Map Rendering Provider
 * (e.g., OpenStreetMap / Osmdroid, MapLibre, Mapbox).
 */
interface MapProvider {
    val providerName: String
    val isConfigured: Boolean
    fun getStatusMessage(): String
}
