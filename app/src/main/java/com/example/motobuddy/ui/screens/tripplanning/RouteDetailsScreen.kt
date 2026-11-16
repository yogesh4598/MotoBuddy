package com.example.motobuddy.ui.screens.tripplanning

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.data.model.Trip
import com.example.motobuddy.data.repository.MotoRepository
import com.example.motobuddy.mapping.OsmRouteProvider
import com.example.motobuddy.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var routeTitle by remember { mutableStateOf("Mahabaleshwar Western Ghats Pass") }
    var origin by remember { mutableStateOf("Pune") }
    var destination by remember { mutableStateOf("Mahabaleshwar") }
    var distanceKm by remember { mutableStateOf(0.0) }
    var durationMins by remember { mutableStateOf(0.0) }
    var isRoutingCalculated by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val routeResult = OsmRouteProvider.calculateRoute(origin, destination)
        distanceKm = routeResult.distanceKm
        durationMins = routeResult.estimatedDurationMinutes
        isRoutingCalculated = routeResult.isRealTimeRouting
    }

    fun launchNavigation(dest: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(dest)}"))
        context.startActivity(Intent.createChooser(intent, "Navigate with:"))
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Route Specifications", color = textWhite, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Main Route Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, primaryCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Landscape, contentDescription = "Pass", tint = secondaryOrange, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("OPENSTRRETMAP ROUTE SPECIFICATIONS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(routeTitle, color = textWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$origin → $destination", color = primaryCyan, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = DividerBorderColor)
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("OSRM Distance", color = textGrey, fontSize = 11.sp)
                            Text(if (distanceKm > 0) String.format(Locale.US, "%.1f km", distanceKm) else "Calculating...", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Est. Duration", color = textGrey, fontSize = 11.sp)
                            Text(if (durationMins > 0) "${(durationMins / 60.0).toInt()}h ${(durationMins % 60).toInt()}m" else "Calculating...", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Est. Fuel", color = textGrey, fontSize = 11.sp)
                            Text(if (distanceKm > 0) String.format(Locale.US, "₹%.0f", (distanceKm / 35.0) * 104.5) else "Calculating...", color = secondaryOrange, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Fuel estimate assumes 35 km/L @ ₹104.5/L", color = textGrey, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Routing Engine Source Badge
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Route, contentDescription = "Engine", tint = primaryCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isRoutingCalculated) "Distance & Travel Duration calculated live via OSRM Routing Engine" else "Querying OSRM routing engine...",
                        color = textWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (isSaved) {
                Text("✓ Route saved to your Saved Trips & MySQL database!", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Save Route Button
            Button(
                onClick = {
                    if (distanceKm > 0) {
                        val trip = Trip(
                            title = routeTitle,
                            startLocation = origin,
                            destination = destination,
                            distanceKm = distanceKm,
                            estDurationHours = durationMins / 60.0,
                            fuelCostEst = (distanceKm / 35.0) * 104.5,
                            date = "Curated Route"
                        )
                        repository.addTrip(trip)
                        isSaved = true
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
                Text("Save Route to My Account", color = MidnightNavy, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { launchNavigation(destination) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Navigation, contentDescription = "Nav", tint = primaryCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Turn-By-Turn Navigation", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
