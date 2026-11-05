package com.example.motobuddy.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.ui.theme.*

data class NotificationItem(
    val title: String,
    val message: String,
    val timestamp: String,
    val icon: ImageVector,
    val iconColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    val notifications = remember {
        mutableStateListOf(
            NotificationItem("SERVICE DUE", "Chain clean & lube recommended at 14,250 km", "10m ago", Icons.Default.Build, secondaryOrange),
            NotificationItem("WEATHER ALERT", "Moderate rainfall expected on Lonavala route after 02:00 PM", "1h ago", Icons.Default.WbCloudy, primaryCyan),
            NotificationItem("SQUAD INVITE", "Aarav invited you to 'Monsoon Lonavala Expedition' group ride", "3h ago", Icons.Default.Group, SuccessGreen),
            NotificationItem("SYSTEM UPDATE", "MotoBuddy v1.0 offline OpenStreetMap routing is active", "1d ago", Icons.Default.Notifications, textGrey)
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Notifications", color = textWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
                    }
                },
                actions = {
                    if (notifications.isNotEmpty()) {
                        IconButton(onClick = { notifications.clear() }) {
                            Icon(imageVector = Icons.Default.ClearAll, contentDescription = "Clear All", tint = textGrey)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No new notifications", color = textGrey, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(notifications) { item ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DividerBorderColor, RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(DarkGraphite),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = item.icon, contentDescription = item.title, tint = item.iconColor, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.title, color = item.iconColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                    Text(item.timestamp, color = textGrey, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.message, color = textWhite, fontSize = 13.sp)
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
}
