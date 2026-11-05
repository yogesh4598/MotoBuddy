package com.example.motobuddy.ui.screens.maintenance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.data.repository.MotoRepository
import com.example.motobuddy.ui.theme.*

data class ServiceScheduleItem(
    val title: String,
    val intervalKm: Int,
    val lastDoneOdoKm: Int,
    val isOverdue: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScheduleScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }
    val motorcycle = remember { repository.getMotorcycle() }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    val scheduleItems = remember(motorcycle.odometerKm) {
        val odo = motorcycle.odometerKm
        listOf(
            ServiceScheduleItem("Chain Lube & Slack Adjustment", intervalKm = 500, lastDoneOdoKm = (odo / 500) * 500, isOverdue = odo % 500 > 400),
            ServiceScheduleItem("Engine Oil & Filter Change", intervalKm = 3000, lastDoneOdoKm = (odo / 3000) * 3000, isOverdue = odo % 3000 > 2800),
            ServiceScheduleItem("Air Filter Inspection / Cleaning", intervalKm = 5000, lastDoneOdoKm = (odo / 5000) * 5000, isOverdue = false),
            ServiceScheduleItem("Spark Plug & Ignition Check", intervalKm = 10000, lastDoneOdoKm = (odo / 10000) * 10000, isOverdue = false),
            ServiceScheduleItem("Brake Fluid Flush & Pad Replacement", intervalKm = 12000, lastDoneOdoKm = (odo / 12000) * 12000, isOverdue = false)
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Service Schedule", color = textWhite, fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 20.dp),
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ACTIVE ODOMETER READING", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${motorcycle.make} ${motorcycle.model} · ${motorcycle.odometerKm} km", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("RECOMMENDED SERVICE INTERVALS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            items(scheduleItems) { item ->
                val kmRemaining = item.intervalKm - (motorcycle.odometerKm % item.intervalKm)
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkGraphite),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (item.isOverdue) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = "Status",
                                    tint = if (item.isOverdue) ErrorRed else SuccessGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.title, color = textWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Interval: Every ${item.intervalKm} km", color = textGrey, fontSize = 12.sp)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (item.isOverdue) "DUE SOON" else "in $kmRemaining km",
                                color = if (item.isOverdue) ErrorRed else primaryCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
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
