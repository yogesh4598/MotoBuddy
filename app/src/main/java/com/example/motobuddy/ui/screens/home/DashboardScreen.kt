package com.example.motobuddy.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.motobuddy.R
import com.example.motobuddy.data.repository.MotoRepository
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.delay

data class CarouselSlide(
    val kicker: String,
    val title: String,
    val subtitle: String,
    val imageRes: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }
    val motorcycle = remember { repository.getMotorcycle() }
    val recentTrip = remember { repository.getSavedTrips().firstOrNull() }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val cardColorLighter = MaterialTheme.colorScheme.surfaceVariant
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Logo",
                        tint = primaryCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MotoBuddy",
                        color = textWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "DASHBOARD",
                    color = textGrey,
                    fontSize = 12.sp,
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Current Location & Rider Greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(cardColor)
                        .border(1.dp, primaryCyan, CircleShape)
                        .clickable { navController.navigate(Screen.MyProfile.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = primaryCyan)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "RIDER: ${motorcycle.ownerName.uppercase()}", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${motorcycle.make} ${motorcycle.model} · ${motorcycle.odometerKm} km", color = textWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = textWhite,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.navigate(Screen.Notification.route) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Auto-Sliding Hero Section (Carousel)
            val slides = listOf(
                CarouselSlide("WEEKEND RECHARGED", "Take the scenic way.", "Discover new roads & scenic mountain passes", R.drawable.moto_landscape),
                CarouselSlide("SMART PLANNING", "Plan Your Dream Ride", "Routes, weather & fuel cost estimation", R.drawable.moto_landscape),
                CarouselSlide("AI MECHANIC", "Troubleshoot on the go", "Instant AI mechanical diagnostics & tips", R.drawable.moto_landscape),
                CarouselSlide("COMMUNITY RIDES", "Ride with the pack", "Join group rides & trip invitations", R.drawable.moto_landscape)
            )
            val pagerState = rememberPagerState(pageCount = { slides.size })

            LaunchedEffect(Unit) {
                while (true) {
                    delay(4000)
                    val nextPage = (pagerState.currentPage + 1) % slides.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardColor)
                    .border(1.dp, cardColorLighter, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        Column {
                            Image(
                                painter = painterResource(id = slides[page].imageRes),
                                contentDescription = slides[page].title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = slides[page].kicker, color = secondaryOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = slides[page].title, color = textWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = slides[page].subtitle, color = textGrey, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pager Indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(slides.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) primaryCyan else Color.DarkGray
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (pagerState.currentPage == iteration) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.TripPlanning.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.AltRoute, contentDescription = "Plan", tint = MidnightNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Plan a Ride", color = MidnightNavy, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { navController.navigate(Screen.FuelCostCalculator.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(imageVector = Icons.Default.LocalGasStation, contentDescription = "Fuel", tint = secondaryOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Fuel Cost", color = textWhite, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "QUICK ACCESS DASHBOARD", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Quick Access Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAccessItem(Icons.Default.SmartToy, "AI Mechanic", "Troubleshoot", accentColor = primaryCyan, modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.MechanicAssistant.route)
                }
                QuickAccessItem(Icons.Default.Build, "Garage", "Service log", accentColor = secondaryOrange, modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.MyBike.route)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAccessItem(Icons.Default.WbSunny, "Weather", "Road status", accentColor = primaryCyan, modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.WeatherRoadConditions.route)
                }
                QuickAccessItem(Icons.Default.LocationOn, "Petrol Pumps", "Maps intent", accentColor = secondaryOrange, modifier = Modifier.weight(1f)) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=petrol+pump"))
                    context.startActivity(intent)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAccessItem(Icons.Default.Security, "Safety SOS", "Emergency", accentColor = ErrorRed, modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.Safety.route)
                }
                QuickAccessItem(Icons.Default.People, "Community", "Group rides", accentColor = primaryCyan, modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.Community.route)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Recent Planned Trip Summary
            if (recentTrip != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Pick up where you left off", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "View trips",
                        color = primaryCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { navController.navigate(Screen.TripHistory.route) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "SAVED ROUTE · ${recentTrip.date}", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "${recentTrip.startLocation} → ${recentTrip.destination}", color = textWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = "${recentTrip.distanceKm.toInt()} km", color = textWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Distance", color = textGrey, fontSize = 12.sp)
                            }
                            Column {
                                Text(text = "₹${recentTrip.fuelCostEst.toInt()}", color = secondaryOrange, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Est. Fuel Cost", color = textGrey, fontSize = 12.sp)
                            }
                            Column {
                                Text(text = "${recentTrip.estDurationHours} hrs", color = textWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Duration", color = textGrey, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { navController.navigate(Screen.TripPlanning.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = cardColorLighter),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Icon(imageVector = Icons.Outlined.BookmarkBorder, contentDescription = "Continue", tint = primaryCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Open Trip Planner", color = textWhite, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun QuickAccessItem(
    icon: ImageVector,
    label: String,
    desc: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DarkGraphite),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = desc, color = TextSecondary, fontSize = 10.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MotoBuddyTheme {
        DashboardScreen(navController = rememberNavController())
    }
}
