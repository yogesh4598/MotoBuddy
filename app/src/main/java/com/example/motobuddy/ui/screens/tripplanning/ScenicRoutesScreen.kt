package com.example.motobuddy.ui.screens.tripplanning

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.R
import com.example.motobuddy.data.model.Trip
import com.example.motobuddy.data.repository.MotoRepository
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.theme.*

data class ScenicRouteItem(
    val title: String,
    val start: String,
    val dest: String,
    val distanceKm: Double,
    val description: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenicRoutesScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    val scenicRoutes = listOf(
        ScenicRouteItem("Mahabaleshwar Western Ghats", "Pune", "Mahabaleshwar", 120.0, "Smooth winding mountain roads through dense forest canopy and lake views.", R.drawable.moto_landscape),
        ScenicRouteItem("Tamhini Ghat Twisties", "Pune", "Tamhini", 75.0, "Popular weekend twisty road with waterfall viewpoints and mountain passes.", R.drawable.moto_landscape),
        ScenicRouteItem("Lonavala Expressway Old Highway", "Pune", "Lonavala", 65.0, "Scenic highway ride with roadside cafes and lush green valley views.", R.drawable.moto_landscape)
    )

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Scenic Rider Routes", color = textWhite, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Landscape, contentDescription = "Scenic", tint = secondaryOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CURATED MOTORCYCLE PASSES", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            items(scenicRoutes) { route ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, primaryCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = route.imageRes),
                            contentDescription = route.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        )
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = route.title, color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "${route.start} → ${route.dest} · ${route.distanceKm.toInt()} km", color = primaryCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = route.description, color = textGrey, fontSize = 12.sp, lineHeight = 16.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val newTrip = Trip(
                                        title = route.title,
                                        startLocation = route.start,
                                        destination = route.dest,
                                        distanceKm = route.distanceKm,
                                        estDurationHours = route.distanceKm / 40.0,
                                        fuelCostEst = (route.distanceKm / 35.0) * 104.5,
                                        date = "Saved Route"
                                    )
                                    repository.addTrip(newTrip)
                                    navController.navigate(Screen.TripHistory.route)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("Plan & Save This Route", color = MidnightNavy, fontWeight = FontWeight.Bold)
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
