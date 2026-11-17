package com.example.motobuddy.ui.screens.maintenance

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.mapping.InAppMapView
import com.example.motobuddy.mapping.LocationHelper
import com.example.motobuddy.mapping.OsmRouteProvider
import com.example.motobuddy.mapping.PlaceSearchResult
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyRepairShopsScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    val shopList = remember { mutableStateListOf<PlaceSearchResult>() }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var manualLocationQuery by remember { mutableStateOf("") }
    var locationStatusText by remember { mutableStateOf("Querying OpenStreetMap Nearby...") }

    var userLat by remember { mutableStateOf(18.5204) }
    var userLon by remember { mutableStateOf(73.8567) }

    fun fetchRepairShops(customLocation: String? = null) {
        coroutineScope.launch {
            isLoading = true
            isError = false
            try {
                val userLoc = LocationHelper.getCurrentLocation(context)
                val results = if (userLoc != null && customLocation.isNullOrBlank()) {
                    userLat = userLoc.latitude
                    userLon = userLoc.longitude
                    locationStatusText = String.format(Locale.US, "GPS Center: %.3f, %.3f", userLoc.latitude, userLoc.longitude)
                    OsmRouteProvider.searchNearbyPlacesWithCoords("motorcycle repair", null, userLoc.latitude, userLoc.longitude)
                } else if (!customLocation.isNullOrBlank()) {
                    locationStatusText = "Search Location: $customLocation"
                    OsmRouteProvider.searchNearbyPlacesWithCoords("motorcycle repair", customLocation, null, null)
                } else {
                    locationStatusText = "Location Permission / GPS Required for Nearby Auto-Search"
                    emptyList()
                }
                shopList.clear()
                shopList.addAll(results)
            } catch (e: Exception) {
                isError = true
            } finally {
                isLoading = false
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchRepairShops()
        }
    }

    LaunchedEffect(Unit) {
        if (LocationHelper.hasLocationPermission(context)) {
            fetchRepairShops()
        } else {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Repair Shops (OpenStreetMap)", color = textWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
                    }
                },
                actions = {
                    IconButton(onClick = { fetchRepairShops(manualLocationQuery.ifBlank { null }) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = primaryCyan)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // In-App Interactive Map Container
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(1.dp, primaryCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    InAppMapView(
                        modifier = Modifier.fillMaxSize(),
                        userLat = userLat,
                        userLon = userLon,
                        poiList = shopList
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = "Garage", tint = secondaryOrange, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("IN-APP REPAIR SHOP MAP SEARCH", color = primaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text(locationStatusText, color = textWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manualLocationQuery,
                        onValueChange = { manualLocationQuery = it },
                        placeholder = { Text("Enter city or area name...", color = textGrey, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = { fetchRepairShops(manualLocationQuery) }) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = primaryCyan)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryCyan, unfocusedBorderColor = cardColor, focusedTextColor = textWhite, unfocusedTextColor = textWhite),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (LocationHelper.hasLocationPermission(context)) {
                                fetchRepairShops()
                            } else {
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MyLocation, contentDescription = "GPS", tint = primaryCyan)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = primaryCyan)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Searching OpenStreetMap for motorcycle repair shops...", color = textGrey, fontSize = 12.sp)
                        }
                    }
                }
            } else if (isError) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Unable to fetch repair shops", color = textWhite, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Network or service error occurred while querying OpenStreetMap.", color = textGrey, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(onClick = { fetchRepairShops(manualLocationQuery.ifBlank { null }) }, colors = ButtonDefaults.buttonColors(containerColor = primaryCyan)) {
                                Text("Retry Search", color = MidnightNavy, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (shopList.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No Repair Shops Found", color = textWhite, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No motorcycle repair places returned by OpenStreetMap for this region. Use 'GPS' or enter a location above.", color = textGrey, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(shopList) { shop ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(shop.name, color = textWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                if (shop.distanceKm != null) {
                                    Text(String.format(Locale.US, "%.1f km", shop.distanceKm), color = primaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(shop.address, color = textGrey, fontSize = 12.sp, maxLines = 2)

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedButton(
                                onClick = { navController.navigate(Screen.RouteSelection.route) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Navigation, contentDescription = "Nav", tint = primaryCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("In-App Route To Repair Shop", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
