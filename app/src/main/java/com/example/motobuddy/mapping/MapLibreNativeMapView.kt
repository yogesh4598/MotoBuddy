package com.example.motobuddy.mapping

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.motobuddy.ui.theme.MidnightNavy
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

object MapLibreConstants {
    const val OPENFREEMAP_LIBERTY_STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"
    val CHENNAI_LAT_LNG = LatLng(13.0827, 80.2707)
    const val DEFAULT_ZOOM = 11.5
}

@Composable
fun MapLibreNativeMapView(
    modifier: Modifier = Modifier,
    styleUrl: String = MapLibreConstants.OPENFREEMAP_LIBERTY_STYLE_URL,
    centerLatLng: LatLng = MapLibreConstants.CHENNAI_LAT_LNG,
    zoomLevel: Double = MapLibreConstants.DEFAULT_ZOOM,
    onMapReady: ((MapLibreMap) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isStyleLoaded by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val mapView = remember {
        MapView(context).apply {
            getMapAsync { mapLibreMap ->
                mapLibreMap.cameraPosition = CameraPosition.Builder()
                    .target(centerLatLng)
                    .zoom(zoomLevel)
                    .build()

                mapLibreMap.uiSettings.setAllGesturesEnabled(true)
                mapLibreMap.uiSettings.isAttributionEnabled = true
                mapLibreMap.uiSettings.isLogoEnabled = true

                mapLibreMap.setStyle(
                    Style.Builder().fromUri(styleUrl)
                ) { style ->
                    isStyleLoaded = true
                    onMapReady?.invoke(mapLibreMap)
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        if (!isStyleLoaded && !isError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0B0F14).copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF00D4FF))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading OpenFreeMap Vector Style (Chennai)...",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
