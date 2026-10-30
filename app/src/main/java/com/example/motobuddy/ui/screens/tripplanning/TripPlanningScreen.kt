package com.example.motobuddy.ui.screens.tripplanning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.R
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlanningScreen(navController: NavController) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EXPLORE",
                    color = textWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search places, routes, cafes...", color = textGrey) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = primaryCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryCyan,
                        unfocusedBorderColor = DividerBorderColor,
                        focusedTextColor = textWhite,
                        unfocusedTextColor = textWhite,
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Categories
            Text(
                text = "CATEGORIES",
                color = textGrey,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { CategoryChip(Icons.Default.Landscape, "Scenic", isSelected = true) }
                item { CategoryChip(Icons.AutoMirrored.Filled.DirectionsBike, "Twisties", isSelected = false) }
                item { CategoryChip(Icons.Default.Restaurant, "Cafes", isSelected = false) }
                item { CategoryChip(Icons.Default.LocationOn, "Off-road", isSelected = false) }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Featured Routes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Popular Routes", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "See all", 
                    color = primaryCyan, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { navController.navigate(Screen.ScenicRoutes.route) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Route Card 1
            RouteCard(
                title = "Mahabaleshwar Twisties",
                subtitle = "Pune · 120 km · Smooth Tarmac",
                imageRes = R.drawable.moto_landscape,
                onClick = { navController.navigate(Screen.RouteDetails.route) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Route Card 2
            RouteCard(
                title = "Lonavala Ghats",
                subtitle = "Pune · 65 km · Scenic View",
                imageRes = R.drawable.moto_landscape,
                onClick = { navController.navigate(Screen.RouteDetails.route) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.RouteSelection.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(text = "Plan New Trip", color = MidnightNavy, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { navController.navigate(Screen.TripHistory.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(text = "Saved Trips", color = textWhite, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun CategoryChip(icon: ImageVector, label: String, isSelected: Boolean) {
    val backgroundColor = if (isSelected) ElectricCyan else CardBackground
    val contentColor = if (isSelected) MidnightNavy else TextPrimary

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = contentColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun RouteCard(title: String, subtitle: String, imageRes: Int, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() }
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = subtitle, color = TextSecondary, fontSize = 12.sp)
                }
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Go", tint = TextSecondary)
            }
        }
    }
}
