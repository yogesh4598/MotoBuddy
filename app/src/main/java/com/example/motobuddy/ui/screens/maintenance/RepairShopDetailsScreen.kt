package com.example.motobuddy.ui.screens.maintenance

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Star
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairShopDetailsScreen(navController: NavController) {
    val context = LocalContext.current

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    fun callShop(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    }

    fun navigateToShop(address: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(address)}"))
        context.startActivity(Intent.createChooser(intent, "Navigate to Repair Shop with:"))
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Service Shop Details", color = textWhite, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Main Details Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, primaryCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("AUTHORIZED MOTORCYCLE SERVICE", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Rating", tint = WarningYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("4.8 (124 reviews)", color = textWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Apex Pro Motorcycle Service Center", color = textWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Open Mon-Sat: 09:00 AM - 08:00 PM · Distance: 2.4 km", color = textGrey, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = DividerBorderColor)
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Address", tint = primaryCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("LOCATION", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Shop #12, Highway Service Road, Kothrud, Pune", color = textWhite, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Phone", tint = primaryCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("CONTACT PHONE", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("+91 98765 43210", color = textWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Services Offered
            Text("SERVICES AVAILABLE", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(12.dp))

            val services = listOf(
                "Synthetic Engine Oil & Filter Replacement",
                "Chain Lube, Cleaning & Slack Adjustment",
                "Brake Pad Flush & Disc Alignment",
                "Full Carburetor / Fuel Injector Cleaning",
                "Emergency Roadside Breakdown Pickup"
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    services.forEach { service ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Build, contentDescription = "Service", tint = secondaryOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(service, color = textWhite, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
            Button(
                onClick = { callShop("+919876543210") },
                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = MidnightNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call Service Shop Now", color = MidnightNavy, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navigateToShop("Kothrud Pune") },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Navigation, contentDescription = "Navigate", tint = primaryCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Navigate To Shop", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
