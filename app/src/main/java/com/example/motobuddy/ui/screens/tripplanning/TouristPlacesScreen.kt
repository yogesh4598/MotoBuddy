package com.example.motobuddy.ui.screens.tripplanning

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.ui.theme.*

data class TouristPointItem(
    val name: String,
    val location: String,
    val description: String,
    val distanceKm: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristPlacesScreen(navController: NavController) {
    val context = LocalContext.current

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    val touristPlaces = listOf(
        TouristPointItem("Arthur's Seat Viewpoint", "Mahabaleshwar", "Panoramic Western Ghats valley cliff view with rider parking area.", "120 km"),
        TouristPointItem("Tiger Point & Waterfall", "Lonavala", "Famous monsoon cliff viewpoint with roadside tea and snack stalls.", "68 km"),
        TouristPointItem("Mulshi Lake Dam Spillway", "Mulshi", "Scenic lakeside touring road popular for weekend motorcycle rides.", "45 km"),
        TouristPointItem("Sinhagad Fort Base Pass", "Pune", "Historic mountain fort base road with steep twisty turns and local food.", "32 km")
    )

    fun navigateToPlace(place: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(place)}"))
        context.startActivity(Intent.createChooser(intent, "Navigate with:"))
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Points of Interest & Viewpoints", color = textWhite, fontWeight = FontWeight.Bold) },
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Place, contentDescription = "Places", tint = secondaryOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("POPULAR RIDER PIT STOPS & VIEWPOINTS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            items(touristPlaces) { place ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, primaryCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(place.name, color = textWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text(place.distanceKm, color = primaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Loc", tint = secondaryOrange, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(place.location, color = textGrey, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(place.description, color = textGrey, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { navigateToPlace("${place.name} ${place.location}") },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Navigation, contentDescription = "Nav", tint = MidnightNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Navigate To Viewpoint", color = MidnightNavy, fontWeight = FontWeight.Bold)
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
