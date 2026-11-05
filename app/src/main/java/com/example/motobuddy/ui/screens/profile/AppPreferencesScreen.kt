package com.example.motobuddy.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPreferencesScreen(navController: NavController) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var useKm by remember { mutableStateOf(true) }
    var offlineMapCache by remember { mutableStateOf(true) }
    var weatherAlerts by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("App Preferences", color = textWhite, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text("UNITS & DISPLAY", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

            // Unit Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardColor)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Distance Units", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(if (useKm) "Kilometers (km)" else "Miles (mi)", color = primaryCyan, fontSize = 12.sp)
                }
                Switch(
                    checked = useKm,
                    onCheckedChange = { useKm = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = MidnightNavy, checkedTrackColor = primaryCyan)
                )
            }

            Text("MAPS & SENSOR DATA", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

            // Offline Map Tile Caching
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardColor)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Offline OpenStreetMap Caching", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Save map tiles locally for no-signal mountain rides", color = textGrey, fontSize = 11.sp)
                }
                Switch(
                    checked = offlineMapCache,
                    onCheckedChange = { offlineMapCache = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = MidnightNavy, checkedTrackColor = primaryCyan)
                )
            }

            Text("NOTIFICATIONS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

            // Weather & Road Warnings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardColor)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Severe Weather & Wet Road Alerts", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Notify when rain or fog is detected on saved routes", color = textGrey, fontSize = 11.sp)
                }
                Switch(
                    checked = weatherAlerts,
                    onCheckedChange = { weatherAlerts = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = MidnightNavy, checkedTrackColor = primaryCyan)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
