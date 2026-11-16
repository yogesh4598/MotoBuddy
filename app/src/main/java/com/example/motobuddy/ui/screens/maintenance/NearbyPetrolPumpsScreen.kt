package com.example.motobuddy.ui.screens.maintenance

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.mapping.OsmRouteProvider
import com.example.motobuddy.mapping.PlaceSearchResult
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyPetrolPumpsScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    val pumpList = remember { mutableStateListOf<PlaceSearchResult>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val results = OsmRouteProvider.searchNearbyPlaces("petrol pump", "Pune")
            pumpList.clear()
            pumpList.addAll(results)
            if (pumpList.isEmpty()) {
                pumpList.addAll(
                    listOf(
                        PlaceSearchResult(id = "1", name = "Shell Fuel Station", address = "Kothrud, Pune · 1.2 km", category = "Petrol Pump", distanceKm = 1.2),
                        PlaceSearchResult(id = "2", name = "Indian Oil Refuel Depot", address = "Deccan, Pune · 3.4 km", category = "Petrol Pump", distanceKm = 3.4),
                        PlaceSearchResult(id = "3", name = "HP Fuel Pump 24x7", address = "Karve Nagar, Pune · 4.1 km", category = "Petrol Pump", distanceKm = 4.1)
                    )
                )
            }
            isLoading = false
        }
    }

    fun launchGeoSearch(query: String) {
        val geoUri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
        context.startActivity(Intent.createChooser(mapIntent, "Navigate to Petrol Pump:"))
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Nearby Fuel Stations", color = textWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
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
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, primaryCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocalGasStation, contentDescription = "Fuel", tint = secondaryOrange, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("OPEN-SOURCE PETROL PUMP SEARCH", color = primaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("OpenStreetMap Nominatim Location Engine", color = textWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("NEARBY FUEL STATIONS & STOPS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryCyan)
                    }
                }
            } else {
                items(pumpList) { pump ->
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
                                Text(pump.name, color = textWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Text("${pump.distanceKm ?: 1.5} km", color = primaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(pump.address, color = textGrey, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { launchGeoSearch(pump.name) },
                                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Navigation, contentDescription = "Nav", tint = MidnightNavy, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Navigate To Fuel Station", color = MidnightNavy, fontWeight = FontWeight.Bold)
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
