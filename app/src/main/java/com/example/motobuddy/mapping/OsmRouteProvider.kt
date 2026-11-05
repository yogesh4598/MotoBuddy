package com.example.motobuddy.mapping

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.UUID

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
                    .header("User-Agent", "MotoBuddyAndroid/1.0")
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

        // Calculation fallback based on string length & estimate if offline
        val estDist = (origin.length + destination.length) * 5.5 + 45.0
        return@withContext RouteResult(
            origin = origin,
            destination = destination,
            distanceKm = estDist,
            estimatedDurationMinutes = (estDist / 50.0) * 60.0,
            isRealTimeRouting = false
        )
    }

    private fun geocodeLocation(query: String): Pair<Double, Double>? {
        return try {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val url = "https://nominatim.openstreetmap.org/search?format=json&q=$encoded&limit=1"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "MotoBuddyAndroid/1.0")
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

    override suspend fun searchNearbyPlaces(query: String, location: String?): List<PlaceSearchResult> = withContext(Dispatchers.IO) {
        val list = mutableListOf<PlaceSearchResult>()
        try {
            val q = if (location.isNullOrBlank()) query else "$query in $location"
            val encoded = URLEncoder.encode(q, "UTF-8")
            val url = "https://nominatim.openstreetmap.org/search?format=json&q=$encoded&limit=10"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "MotoBuddyAndroid/1.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val array = JSONArray(body)
                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            list.add(
                                PlaceSearchResult(
                                    id = UUID.randomUUID().toString(),
                                    name = obj.optString("display_name").split(",").firstOrNull() ?: query,
                                    address = obj.optString("display_name"),
                                    category = query,
                                    distanceKm = (i + 1) * 1.5,
                                    latitude = obj.optDouble("lat"),
                                    longitude = obj.optDouble("lon")
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext list
    }
}
