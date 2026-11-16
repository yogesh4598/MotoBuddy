package com.example.motobuddy.mapping

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.UUID
import kotlin.math.*

object OsmRouteProvider : MapProvider, RouteProvider, PlaceSearchProvider {
    override val providerName: String = "OpenStreetMap / OSRM Open Routing Engine"
    override val isConfigured: Boolean = true

    private val client = OkHttpClient()

    override fun getStatusMessage(): String {
        return "Connected to OpenStreetMap Nominatim & OSRM Routing Engine"
    }

    override suspend fun calculateRoute(origin: String, destination: String): RouteResult = withContext(Dispatchers.IO) {
        try {
            val originCoord = geocodeLocation(origin)
            val destCoord = geocodeLocation(destination)

            if (originCoord != null && destCoord != null) {
                val url = "https://router.project-osrm.org/route/v1/driving/${originCoord.second},${originCoord.first};${destCoord.second},${destCoord.first}?overview=false"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "MotoBuddyAndroid/1.0 (contact@motobuddy.app)")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (!body.isNullOrBlank()) {
                            val json = JSONObject(body)
                            val routes = json.optJSONArray("routes")
                            if (routes != null && routes.length() > 0) {
                                val route = routes.getJSONObject(0)
                                val distanceMeters = route.optDouble("distance", 0.0)
                                val durationSeconds = route.optDouble("duration", 0.0)

                                return@withContext RouteResult(
                                    origin = origin,
                                    destination = destination,
                                    distanceKm = distanceMeters / 1000.0,
                                    estimatedDurationMinutes = durationSeconds / 60.0,
                                    isRealTimeRouting = true,
                                    points = listOf(
                                        RoutePoint(originCoord.first, originCoord.second, origin),
                                        RoutePoint(destCoord.first, destCoord.second, destination)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Return unconfigured empty route result if geocoding/routing fails (no fake calculations)
        return@withContext RouteResult(
            origin = origin,
            destination = destination,
            distanceKm = 0.0,
            estimatedDurationMinutes = 0.0,
            isRealTimeRouting = false
        )
    }

    private fun geocodeLocation(query: String): Pair<Double, Double>? {
        return try {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val url = "https://nominatim.openstreetmap.org/search?format=json&q=$encoded&limit=1"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "MotoBuddyAndroid/1.0 (contact@motobuddy.app)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val array = JSONArray(body)
                        if (array.length() > 0) {
                            val obj = array.getJSONObject(0)
                            val lat = obj.getDouble("lat")
                            val lon = obj.getDouble("lon")
                            return Pair(lat, lon)
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun searchNearbyPlaces(query: String, location: String?): List<PlaceSearchResult> = searchNearbyPlacesWithCoords(query, location, null, null)

    suspend fun searchNearbyPlacesWithCoords(query: String, location: String?, userLat: Double?, userLon: Double?): List<PlaceSearchResult> = withContext(Dispatchers.IO) {
        val list = mutableListOf<PlaceSearchResult>()
        try {
            val q = if (location.isNullOrBlank()) query else "$query in $location"
            val encoded = URLEncoder.encode(q, "UTF-8")
            val url = if (userLat != null && userLon != null) {
                "https://nominatim.openstreetmap.org/search?format=json&q=$encoded&lat=$userLat&lon=$userLon&limit=15"
            } else {
                "https://nominatim.openstreetmap.org/search?format=json&q=$encoded&limit=15"
            }

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "MotoBuddyAndroid/1.0 (contact@motobuddy.app)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val array = JSONArray(body)
                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            val placeLat = obj.optDouble("lat", 0.0)
                            val placeLon = obj.optDouble("lon", 0.0)
                            val dist = if (userLat != null && userLon != null && placeLat != 0.0 && placeLon != 0.0) {
                                haversineKm(userLat, userLon, placeLat, placeLon)
                            } else {
                                null
                            }

                            list.add(
                                PlaceSearchResult(
                                    id = UUID.randomUUID().toString(),
                                    name = obj.optString("display_name").split(",").firstOrNull() ?: query,
                                    address = obj.optString("display_name"),
                                    category = query,
                                    distanceKm = dist,
                                    latitude = placeLat,
                                    longitude = placeLon
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext list.sortedBy { it.distanceKm ?: Double.MAX_VALUE }
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
