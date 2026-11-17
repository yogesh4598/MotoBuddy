package com.example.motobuddy.mapping

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InAppMapView(
    modifier: Modifier = Modifier,
    userLat: Double = 18.5204,
    userLon: Double = 73.8567,
    routePoints: List<RoutePoint> = emptyList(),
    poiList: List<PlaceSearchResult> = emptyList(),
    onMarkerClick: ((PlaceSearchResult) -> Unit)? = null
) {
    var webViewRef: WebView? = null

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewRef = this
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                setInitialScale(1)

                webViewClient = WebViewClient()

                val htmlContent = buildMapHtml(userLat, userLon)
                loadDataWithBaseURL("https://motobuddy.app", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webViewRef = webView
            if (routePoints.isNotEmpty()) {
                val pointsJson = routePoints.joinToString(prefix = "[", postfix = "]") { pt ->
                    "[${pt.latitude}, ${pt.longitude}]"
                }
                webView.evaluateJavascript("javascript:drawRoutePolyline($pointsJson);", null)
            }
            if (poiList.isNotEmpty()) {
                val poisJson = poiList.joinToString(prefix = "[", postfix = "]") { poi ->
                    val lat = poi.latitude ?: userLat
                    val lon = poi.longitude ?: userLon
                    "{\"name\": \"${poi.name.replace("\"", "\\\"")}\", \"lat\": $lat, \"lon\": $lon, \"category\": \"${poi.category}\"}"
                }
                webView.evaluateJavascript("javascript:renderPoiMarkers($poisJson);", null)
            }
        }
    )
}

private fun buildMapHtml(lat: Double, lon: Double): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                html, body, #map { height: 100%; width: 100%; margin: 0; padding: 0; background: #0B0F14; }
                .leaflet-container { background: #0B0F14; }
                .rider-pulse {
                    width: 18px; height: 18px; background: #00D4FF; border: 3px solid #0B0F14;
                    border-radius: 50%; box-shadow: 0 0 12px #00D4FF;
                }
                .poi-pin {
                    width: 14px; height: 14px; background: #FF6B00; border: 2px solid #FFFFFF;
                    border-radius: 50%; box-shadow: 0 0 8px #FF6B00;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', { zoomControl: false, attributionControl: false }).setView([$lat, $lon], 13);
                
                // CartoDB Dark Matter Vector Tile Layer (MotoBuddy Dark Theme)
                L.tileLayer('https://basemaps.cartocdn.com/dark_all/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    subdomains: 'abcd'
                }).addTo(map);

                // Rider Marker
                var riderIcon = L.divIcon({ className: 'rider-pulse', iconSize: [18, 18], iconAnchor: [9, 9] });
                var riderMarker = L.marker([$lat, $lon], { icon: riderIcon }).addTo(map);

                var currentPolyline = null;
                var poiLayerGroup = L.layerGroup().addTo(map);

                function drawRoutePolyline(points) {
                    if (currentPolyline) map.removeLayer(currentPolyline);
                    if (!points || points.length === 0) return;

                    currentPolyline = L.polyline(points, {
                        color: '#00D4FF',
                        weight: 5,
                        opacity: 0.95,
                        lineCap: 'round',
                        lineJoin: 'round'
                    }).addTo(map);

                    map.fitBounds(currentPolyline.getBounds(), { padding: [40, 40] });
                }

                function renderPoiMarkers(pois) {
                    poiLayerGroup.clearLayers();
                    if (!pois) return;
                    pois.forEach(function(p) {
                        var pIcon = L.divIcon({ className: 'poi-pin', iconSize: [14, 14], iconAnchor: [7, 7] });
                        var m = L.marker([p.lat, p.lon], { icon: pIcon });
                        m.bindPopup("<b style='color:#0B0F14'>" + p.name + "</b>");
                        poiLayerGroup.addLayer(m);
                    });
                }

                function recenterMap(lat, lon) {
                    riderMarker.setLatLng([lat, lon]);
                    map.panTo([lat, lon]);
                }
            </script>
        </body>
        </html>
    """.trimIndent()
}
