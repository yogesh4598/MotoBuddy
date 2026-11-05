package com.example.motobuddy.ui.screens.maintenance

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.data.repository.MotoRepository
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeDiagnosticsScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }
    val motorcycle = remember { repository.getMotorcycle() }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Vehicle Health & Diagnostics", color = textWhite, fontWeight = FontWeight.Bold) },
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

            // Vehicle Status Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, primaryCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("VEHICLE STATUS REPORT", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${motorcycle.make} ${motorcycle.model}", color = textWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Odometer: ${motorcycle.odometerKm} km", color = primaryCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DividerBorderColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Overall Diagnostic Status", color = textGrey, fontSize = 12.sp)
                        Text("HEALTHY (0 ERRORS)", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("SYSTEM METRICS & VITAL SIGNS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            // Metric 1: Battery
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = "Battery", tint = primaryCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Battery Terminal Voltage", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("12.6V DC · Charging System Normal", color = textGrey, fontSize = 11.sp)
                    }
                    Text("GOOD", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metric 2: Engine Temp
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Thermostat, contentDescription = "Temp", tint = secondaryOrange, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Operating Temperature", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("88°C · Coolant Flow Optimal", color = textGrey, fontSize = 11.sp)
                    }
                    Text("OPTIMAL", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metric 3: Drive Chain & Brakes
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Build, contentDescription = "Drive Chain", tint = primaryCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Drive Chain Slack & Pads", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Slack: 28mm · Front Pad Wear: 75% Life", color = textGrey, fontSize = 11.sp)
                    }
                    Text("OK", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // AI Mechanic Diagnostic Scan Button
            Button(
                onClick = { navController.navigate(Screen.MechanicAssistant.route) },
                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.SmartToy, contentDescription = "Scan", tint = MidnightNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open AI Diagnostic Assistant", color = MidnightNavy, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
