package com.example.motobuddy.ui.screens.community

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripSharingScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }
    val motorcycle = remember { repository.getMotorcycle() }
    val recentTrip = remember { repository.getSavedTrips().firstOrNull() }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var isLiveSharingActive by remember { mutableStateOf(true) }

    fun shareLiveTrip() {
        val tripTitle = recentTrip?.title ?: "Pune to Mahabaleshwar Ride"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "🏍️ Track my live motorcycle ride on MotoBuddy!\nRoute: $tripTitle\nMotorcycle: ${motorcycle.make} ${motorcycle.model}\nLive Tracking Link: https://motobuddy.app/track/ride_live_789"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Live Ride via:"))
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Live Location & Trip Sharing", color = textWhite, fontWeight = FontWeight.Bold) },
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

            // Safety Banner
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
                    Icon(imageVector = Icons.Default.Shield, contentDescription = "Shield", tint = SuccessGreen, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("SQUAD SAFETY TRACKING", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Share your real-time GPS coordinates with family and squad members while riding.", color = textGrey, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Live Sharing Toggle Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardColor)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Live GPS Broadcast", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(if (isLiveSharingActive) "Active · Updating every 10 sec" else "Disabled", color = if (isLiveSharingActive) SuccessGreen else textGrey, fontSize = 12.sp)
                }
                Switch(
                    checked = isLiveSharingActive,
                    onCheckedChange = { isLiveSharingActive = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = MidnightNavy, checkedTrackColor = primaryCyan)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Active Route Card
            Text("ACTIVE ROUTE TO BROADCAST", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Loc", tint = secondaryOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(recentTrip?.title ?: "Pune to Mahabaleshwar Ride", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Motorcycle: ${motorcycle.make} ${motorcycle.model}", color = textGrey, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Distance: ${recentTrip?.distanceKm?.toInt() ?: 120} km · Est. Fuel: ₹${recentTrip?.fuelCostEst?.toInt() ?: 420}", color = primaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DividerBorderColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("SHAREABLE TRACKING LINK", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("https://motobuddy.app/track/ride_live_789", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Share Button
            Button(
                onClick = { shareLiveTrip() },
                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = MidnightNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Live Trip Link", color = MidnightNavy, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
