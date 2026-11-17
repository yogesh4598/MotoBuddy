package com.example.motobuddy.ui.screens.tripplanning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.mapping.LocationHelper
import com.example.motobuddy.mapping.MapLibreConstants
import com.example.motobuddy.mapping.MapLibreNativeMapView
import com.example.motobuddy.mapping.OsmRouteProvider
import com.example.motobuddy.mapping.RoutePoint
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveNavigationScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var centerLat by remember { mutableStateOf(13.0827) }
    var centerLon by remember { mutableStateOf(80.2707) }
    var routePointsList by remember { mutableStateOf<List<RoutePoint>>(emptyList()) }
    var routeTitle by remember { mutableStateOf("Chennai to Pune Route") }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val loc = LocationHelper.getCurrentLocation(context)
            if (loc != null) {
                centerLat = loc.latitude
                centerLon = loc.longitude
            }
            val route = OsmRouteProvider.calculateRoute("Chennai", "Pune")
            routePointsList = route.points
            if (route.distanceKm > 0) {
                routeTitle = "Chennai → Pune (${route.distanceKm.toInt()} km)"
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("MapLibre Navigation Canvas", color = textWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Full Screen Native MapLibre Canvas with OpenFreeMap Style
            MapLibreNativeMapView(
                modifier = Modifier.fillMaxSize(),
                styleUrl = MapLibreConstants.OPENFREEMAP_LIBERTY_STYLE_URL,
                centerLatLng = LatLng(centerLat, centerLon),
                zoomLevel = MapLibreConstants.DEFAULT_ZOOM,
                routePoints = routePointsList,
                originName = "Chennai",
                destName = "Pune"
            )

            // Top Floating Route Status Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = primaryCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MidnightNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.AltRoute, contentDescription = "Route", tint = primaryCyan, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("ACTIVE OSRM ROUTE", color = MidnightNavy, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        Text(routeTitle, color = MidnightNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottom Navigation Control Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, primaryCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("IN-APP NAVIGATION MODE", color = primaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Real-Time Route Preview Active (OpenFreeMap Liberty Style)", color = textWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Turn-By-Turn Voice Engine in Development", color = textGrey, fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = "End", tint = textWhite)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exit Navigation Canvas", color = textWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
