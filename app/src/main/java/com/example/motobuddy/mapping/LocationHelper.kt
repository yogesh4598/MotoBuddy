package com.example.motobuddy.mapping

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

object LocationHelper {
    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun isGpsEnabled(context: Context): Boolean {
        val locManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? = withContext(Dispatchers.Main) {
        if (!hasLocationPermission(context)) return@withContext null

        val locManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return@withContext null

        val gpsLoc = try { locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (e: Exception) { null }
        if (gpsLoc != null && (System.currentTimeMillis() - gpsLoc.time) < 120000) {
            return@withContext gpsLoc
        }

        val netLoc = try { locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (e: Exception) { null }
        if (netLoc != null && (System.currentTimeMillis() - netLoc.time) < 120000) {
            return@withContext netLoc
        }

        suspendCancellableCoroutine { continuation ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    try { locManager.removeUpdates(this) } catch (e: Exception) {}
                    if (continuation.isActive) continuation.resume(location)
                }

                @Deprecated("Deprecated in API 29")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {
                    if (continuation.isActive) continuation.resume(gpsLoc ?: netLoc)
                }
            }

            try {
                if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null)
                } else if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null)
                } else {
                    if (continuation.isActive) continuation.resume(gpsLoc ?: netLoc)
                }
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(gpsLoc ?: netLoc)
            }
        }
    }
}
