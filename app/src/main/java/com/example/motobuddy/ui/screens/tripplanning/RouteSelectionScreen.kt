package com.example.motobuddy.ui.screens.tripplanning

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.data.model.Trip
import com.example.motobuddy.data.repository.MotoRepository
import com.example.motobuddy.mapping.MapLibreConstants
import com.example.motobuddy.mapping.MapLibreNativeMapView
import com.example.motobuddy.mapping.OsmRouteProvider
import com.example.motobuddy.mapping.RoutePoint
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }
    val motorcycle = remember { repository.getMotorcycle() }
    val fuelLogs = remember { repository.getFuelEntries() }
    val avgMileage = remember { repository.calculateAverageMileage(fuelLogs) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var startLoc by remember { mutableStateOf("Pune") }
    var destLoc by remember { mutableStateOf("Mahabaleshwar") }
    var customMileageInput by remember { mutableStateOf(String.format(Locale.US, "%.1f", avgMileage)) }
    var customFuelPriceInput by remember { mutableStateOf("104.5") }
    var tripSavedSuccess by remember { mutableStateOf(false) }

    var isCalculating by remember { mutableStateOf(false) }
    var calculatedDistKm by remember { mutableStateOf(0.0) }
    var calculatedDurationMins by remember { mutableStateOf(0.0) }
    var routePointsList by remember { mutableStateOf<List<RoutePoint>>(emptyList()) }
    var isOsmRoute by remember { mutableStateOf(false) }
    var routingErrorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(startLoc, destLoc) {
        if (startLoc.isNotBlank() && destLoc.isNotBlank()) {
            isCalculating = true
            routingErrorMsg = null
            val route = OsmRouteProvider.calculateRoute(startLoc.trim(), destLoc.trim())
            if (route.isRealTimeRouting && route.distanceKm > 0.0) {
                calculatedDistKm = route.distanceKm
                calculatedDurationMins = route.estimatedDurationMinutes
                routePointsList = route.points
                isOsmRoute = true
            } else {
                isOsmRoute = false
                calculatedDistKm = 0.0
                calculatedDurationMins = 0.0
                routePointsList = emptyList()
                routingErrorMsg = "Unable to find route for '$startLoc' → '$destLoc'. Please verify location names."
            }
            isCalculating = false
        }
    }

    val parsedMileage = remember(customMileageInput) { customMileageInput.toDoubleOrNull() ?: avgMileage }
    val parsedFuelPrice = remember(customFuelPriceInput) { customFuelPriceInput.toDoubleOrNull() ?: 104.5 }

    val distanceKm = calculatedDistKm
    val estHours = remember(calculatedDurationMins) { String.format(Locale.US, "%.1f", calculatedDurationMins / 60.0).toDoubleOrNull() ?: 0.0 }
    val fuelLiters = remember(distanceKm, parsedMileage) { if (parsedMileage > 0) distanceKm / parsedMileage else 0.0 }
    val fuelCostEst = remember(fuelLiters, parsedFuelPrice) { fuelLiters * parsedFuelPrice }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Plan Custom Route", color = textWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Native MapLibre OpenFreeMap Vector Map Container
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .border(1.dp, primaryCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                MapLibreNativeMapView(
                    modifier = Modifier.fillMaxSize(),
                    styleUrl = MapLibreConstants.OPENFREEMAP_LIBERTY_STYLE_URL,
                    centerLatLng = MapLibreConstants.CHENNAI_LAT_LNG,
                    zoomLevel = MapLibreConstants.DEFAULT_ZOOM
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // OpenFreeMap Liberty Vector Style Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Navigation, contentDescription = "OSM", tint = primaryCyan)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("MapLibre Native + OpenFreeMap Liberty", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = "Style: https://tiles.openfreemap.org/styles/liberty (Chennai, IN)",
                            color = primaryCyan,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text Inputs
            OutlinedTextField(
                value = startLoc,
                onValueChange = { startLoc = it },
                label = { Text("Start Location", color = textGrey) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryCyan,
                    unfocusedBorderColor = DividerBorderColor,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = destLoc,
                onValueChange = { destLoc = it },
                label = { Text("Destination", color = textGrey) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryCyan,
                    unfocusedBorderColor = DividerBorderColor,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Motorcycle & Fuel Configuration Card
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.DirectionsBike, contentDescription = "Bike", tint = secondaryOrange, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GARAGE MOTORCYCLE & FUEL COST PARAMS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${motorcycle.make} ${motorcycle.model}", color = textWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = customMileageInput,
                            onValueChange = { customMileageInput = it },
                            label = { Text("Mileage (km/L)", color = textGrey, fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryCyan, unfocusedBorderColor = DividerBorderColor, focusedTextColor = textWhite, unfocusedTextColor = textWhite),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customFuelPriceInput,
                            onValueChange = { customFuelPriceInput = it },
                            label = { Text("Fuel Price (₹/L)", color = textGrey, fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryCyan, unfocusedBorderColor = DividerBorderColor, focusedTextColor = textWhite, unfocusedTextColor = textWhite),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Route Calculation Result Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, primaryCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("OSRM ROUTE SUMMARY", color = textGrey, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$startLoc → $destLoc", color = textWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Distance", color = textGrey, fontSize = 11.sp)
                            Text(if (distanceKm > 0) "${distanceKm.toInt()} km" else "N/A", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Duration", color = textGrey, fontSize = 11.sp)
                            Text(if (distanceKm > 0) "$estHours hrs" else "N/A", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Est. Fuel Cost", color = textGrey, fontSize = 11.sp)
                            Text(if (distanceKm > 0) String.format(Locale.US, "₹%.0f", fuelCostEst) else "N/A", color = secondaryOrange, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (distanceKm > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Est. ${String.format(Locale.US, "%.1f", fuelLiters)} Liters fuel @ ₹$parsedFuelPrice/L for $parsedMileage km/L", color = textGrey, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (tripSavedSuccess) {
                Text("✓ Trip saved to your Saved Trips & MySQL database!", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Save Trip Button
            Button(
                onClick = {
                    if (destLoc.isNotBlank() && distanceKm > 0) {
                        val newTrip = Trip(
                            title = "$startLoc to $destLoc Route",
                            startLocation = startLoc,
                            destination = destLoc,
                            distanceKm = distanceKm,
                            estDurationHours = estHours,
                            fuelCostEst = fuelCostEst,
                            date = "Planned"
                        )
                        repository.addTrip(newTrip)
                        tripSavedSuccess = true
                    }
                },
                enabled = distanceKm > 0,
                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = "Save", tint = MidnightNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Trip to My Account", color = MidnightNavy, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Start In-App Live Navigation
            Button(
                onClick = { navController.navigate(Screen.LiveNavigation.route) },
                enabled = destLoc.isNotBlank() && distanceKm > 0,
                colors = ButtonDefaults.buttonColors(containerColor = secondaryOrange),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Navigation, contentDescription = "Navigate", tint = MidnightNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start In-App Live Navigation", color = MidnightNavy, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
