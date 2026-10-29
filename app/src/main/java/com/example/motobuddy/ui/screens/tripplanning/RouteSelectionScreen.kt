package com.example.motobuddy.ui.screens.tripplanning

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.data.model.Trip
import com.example.motobuddy.data.repository.MotoRepository
import com.example.motobuddy.mapping.UnconfiguredMappingEngine
import com.example.motobuddy.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var startLoc by remember { mutableStateOf("Pune") }
    var destLoc by remember { mutableStateOf("Mahabaleshwar") }
    var distanceInput by remember { mutableStateOf("120") }
    var tripSavedSuccess by remember { mutableStateOf(false) }

    val distanceKm = remember(distanceInput) { distanceInput.toDoubleOrNull() ?: 120.0 }
    val estHours = remember(distanceKm) { String.format(Locale.US, "%.1f", distanceKm / 40.0).toDoubleOrNull() ?: 3.0 }
    val fuelCostEst = remember(distanceKm) { (distanceKm / 35.0) * 104.5 }

    fun launchExternalNavigation(destination: String) {
        val geoUri = Uri.parse("geo:0,0?q=${Uri.encode(destination)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
        context.startActivity(Intent.createChooser(mapIntent, "Open Navigation with:"))
    }

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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Open Source Mapping Notice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardColor)
                    .border(1.dp, primaryCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = primaryCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Map Engine: Open-Source Architecture", color = textWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(UnconfiguredMappingEngine.getStatusMessage(), color = textGrey, fontSize = 11.sp, lineHeight = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = startLoc,
                onValueChange = { startLoc = it },
                label = { Text("Starting Location", color = textGrey) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Start", tint = primaryCyan) },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite, focusedBorderColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = destLoc,
                onValueChange = { destLoc = it },
                label = { Text("Destination", color = textGrey) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Dest", tint = secondaryOrange) },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite, focusedBorderColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = distanceInput,
                onValueChange = { distanceInput = it },
                label = { Text("Estimated Distance (km)", color = textGrey) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite, focusedBorderColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Summary Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, primaryCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("ROUTE SUMMARY", color = textGrey, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$startLoc → $destLoc", color = textWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Distance", color = textGrey, fontSize = 11.sp)
                            Text("${distanceKm.toInt()} km", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Duration", color = textGrey, fontSize = 11.sp)
                            Text("$estHours hrs", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Est. Fuel Cost", color = textGrey, fontSize = 11.sp)
                            Text("₹${fuelCostEst.toInt()}", color = secondaryOrange, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (tripSavedSuccess) {
                Text("✓ Trip saved to your Saved Trips!", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Save Trip Button
            Button(
                onClick = {
                    if (destLoc.isNotBlank()) {
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
                colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Save", tint = primaryCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save to My Trips", color = textWhite, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Handoff Button
            Button(
                onClick = { launchExternalNavigation(destLoc) },
                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Navigation, contentDescription = "Start", tint = MidnightNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Launch External Navigation", color = MidnightNavy, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
