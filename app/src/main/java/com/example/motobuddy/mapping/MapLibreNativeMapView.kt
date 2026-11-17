package com.example.motobuddy.mapping

import android.graphics.Color
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

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
    routePoints: List<RoutePoint> = emptyList(),
    originName: String = "Start",
    destName: String = "Destination",
    onMapReady: ((MapLibreMap) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isStyleLoaded by remember { mutableStateOf(false) }
    var activeMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var activeStyle by remember { mutableStateOf<Style?>(null) }

    val mapView = remember {
        MapView(context).apply {
            getMapAsync { mapLibreMap ->
                activeMap = mapLibreMap
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
                    activeStyle = style
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

    // Render Route Geometry Polyline & Start/Dest Markers on MapLibre Native Canvas
    LaunchedEffect(routePoints, isStyleLoaded, activeMap, activeStyle) {
        val map = activeMap
        val style = activeStyle

        if (map != null && style != null && isStyleLoaded) {
            map.clear() // Clear existing markers

            if (routePoints.isNotEmpty()) {
                val geoPoints = routePoints.map { Point.fromLngLat(it.longitude, it.latitude) }
                val latLngList = routePoints.map { LatLng(it.latitude, it.longitude) }

                val lineString = LineString.fromLngLats(geoPoints)
                val sourceId = "osrm-route-source"
                val layerId = "osrm-route-layer"

                val existingSource = style.getSource(sourceId) as? GeoJsonSource
                if (existingSource != null) {
                    existingSource.setGeoJson(lineString)
                } else {
                    val routeSource = GeoJsonSource(sourceId, lineString)
                    style.addSource(routeSource)

                    val routeLayer = LineLayer(layerId, sourceId).apply {
                        setProperties(
                            PropertyFactory.lineColor(Color.parseColor("#00D4FF")),
                            PropertyFactory.lineWidth(6f),
                            PropertyFactory.lineOpacity(0.9f)
                        )
                    }
                    style.addLayer(routeLayer)
                }

                // Add Start & Destination Markers
                val startPt = latLngList.first()
                val destPt = latLngList.last()

                map.addMarker(MarkerOptions().position(startPt).title("Start: $originName"))
                map.addMarker(MarkerOptions().position(destPt).title("Destination: $destName"))

                // Animate camera to fit route bounds
                if (latLngList.size > 1) {
                    val bounds = LatLngBounds.Builder().includes(latLngList).build()
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))
                }
            } else {
                // If route cleared, remove route layer
                val existingSource = style.getSource("osrm-route-source") as? GeoJsonSource
                existingSource?.setGeoJson(LineString.fromLngLats(emptyList()))
            }
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        if (!isStyleLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color(0xFF0B0F14).copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = androidx.compose.ui.graphics.Color(0xFF00D4FF))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading OpenFreeMap Vector Style (Liberty)...",
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
