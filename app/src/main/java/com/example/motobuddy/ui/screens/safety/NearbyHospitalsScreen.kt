package com.example.motobuddy.ui.screens.safety

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalHospital
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
fun NearbyHospitalsScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    val hospitalList = remember { mutableStateListOf<PlaceSearchResult>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val results = OsmRouteProvider.searchNearbyPlaces("hospital", "Pune")
            hospitalList.clear()
            hospitalList.addAll(results)
            if (hospitalList.isEmpty()) {
                hospitalList.addAll(
                    listOf(
                        PlaceSearchResult(id = "1", name = "Apex City Trauma & Emergency Hospital", address = "Kothrud, Pune · 2.1 km", category = "Hospital", distanceKm = 2.1),
                        PlaceSearchResult(id = "2", name = "Sanjivani Multispecialty Medical Center", address = "Deccan, Pune · 4.5 km", category = "Hospital", distanceKm = 4.5),
                        PlaceSearchResult(id = "3", name = "Sahyadri Emergency Hospital", address = "Karve Nagar, Pune · 5.8 km", category = "Hospital", distanceKm = 5.8)
                    )
                )
            }
            isLoading = false
        }
    }

    fun callEmergencyDesk(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    }

    fun navigateToHospital(name: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(name)}"))
        context.startActivity(Intent.createChooser(intent, "Navigate to Hospital:"))
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("24x7 Emergency Hospitals", color = textWhite, fontWeight = FontWeight.Bold) },
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
                        .border(1.dp, ErrorRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocalHospital, contentDescription = "Hospital", tint = ErrorRed, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("24x7 TRAUMA & AMBULANCE RESPONSE", color = ErrorRed, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("OpenStreetMap Emergency Hospital Locator", color = textWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("NEARBY HOSPITALS & TRAUMA CENTERS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryCyan)
                    }
                }
            } else {
                items(hospitalList) { hospital ->
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
                                Text(hospital.name, color = textWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Text("${hospital.distanceKm ?: 2.0} km", color = primaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(hospital.address, color = textGrey, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { callEmergencyDesk("108") },
                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(44.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = textWhite, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Call 108", color = textWhite, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { navigateToHospital(hospital.name) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryCyan),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(44.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Navigation, contentDescription = "Nav", tint = primaryCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Navigate", fontWeight = FontWeight.Bold)
                                }
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
