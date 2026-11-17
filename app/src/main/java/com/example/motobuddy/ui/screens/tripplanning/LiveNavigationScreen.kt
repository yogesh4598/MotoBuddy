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
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Speed
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
import com.example.motobuddy.mapping.InAppMapView
import com.example.motobuddy.mapping.LocationHelper
import com.example.motobuddy.mapping.OsmRouteProvider
import com.example.motobuddy.mapping.RoutePoint
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveNavigationScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var userLat by remember { mutableStateOf(18.5204) }
    var userLon by remember { mutableStateOf(73.8567) }
    var routePointsList by remember { mutableStateOf<List<RoutePoint>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val loc = LocationHelper.getCurrentLocation(context)
            if (loc != null) {
                userLat = loc.latitude
                userLon = loc.longitude
            }
            val route = OsmRouteProvider.calculateRoute("Pune", "Mahabaleshwar")
            routePointsList = route.points
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("In-App Live Navigation", color = textWhite, fontWeight = FontWeight.Bold) },
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
            // Full Screen Uber-Style Interactive Map
            InAppMapView(
                modifier = Modifier.fillMaxSize(),
                userLat = userLat,
                userLon = userLon,
                routePoints = routePointsList
            )

            // Top Floating Turn Instruction Banner
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
                        Icon(imageVector = Icons.AutoMirrored.Filled.AltRoute, contentDescription = "Right", tint = primaryCyan, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("IN 300 METERS", color = MidnightNavy, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Turn Right onto Ghat Highway", color = MidnightNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottom Expandable Navigation Controls & Speedometer HUD
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Speed, contentDescription = "Speed", tint = secondaryOrange, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("62 KM/H", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SuccessGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("LIMIT 80", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

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
                            Text("End Live Navigation", color = textWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
