package com.example.motobuddy.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.motobuddy.R
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.theme.MotoBuddyTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val backgroundColor = Color(0xFF09090B)
    val cardColor = Color(0xFF18181B)
    val cardColorLighter = Color(0xFF27272A)
    val textWhite = Color(0xFFFAFAFA)
    val textGrey = Color(0xFFA1A1AA)
    val primaryOrange = Color(0xFFFF4500)

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
                        tint = primaryOrange,
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
                    text = "DASHBOARD / 04",
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

            // Current Location Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(cardColor)
                        .clickable { navController.navigate(Screen.MyProfile.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = textGrey)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "CURRENT LOCATION", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Pune, Maharashtra · Aarav", color = textWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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
                CarouselSlide("WEEKEND, RECHARGED", "Take the scenic way.", "Discover new roads & cafes", R.drawable.moto_landscape),
                CarouselSlide("SMART PLANNING", "Plan Your Dream Ride", "Routes, weather & fuel costs", R.drawable.moto_landscape),
                CarouselSlide("AI MECHANIC", "Troubleshoot on the go", "Instant repair assistance", R.drawable.moto_landscape),
                CarouselSlide("COMMUNITY", "Ride with the pack", "Join group rides & events", R.drawable.moto_landscape)
            )
            val pagerState = rememberPagerState(pageCount = { slides.size })

            LaunchedEffect(Unit) {
                while (true) {
                    delay(3500)
                    val nextPage = (pagerState.currentPage + 1) % slides.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardColor)
                    .padding(20.dp)
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
                            Text(text = slides[page].kicker, color = primaryOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = slides[page].title, color = textWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = slides[page].subtitle, color = textGrey, fontSize = 14.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Pager Indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(slides.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) primaryOrange else Color.DarkGray
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
                    colors = ButtonDefaults.buttonColors(containerColor = primaryOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.AltRoute, contentDescription = "Plan", tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Plan a Ride", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { navController.navigate(Screen.Search.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.BookmarkBorder, contentDescription = "Search", tint = textWhite, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Quick Search", color = textWhite, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "QUICK ACCESS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Quick Access Grid (Improved Accessibility & Practicality)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAccessItem(Icons.Default.LocalGasStation, "Fuel", "Log & track", modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.FuelCostCalculator.route)
                }
                QuickAccessItem(Icons.Default.Build, "Garage", "Bike health", modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.MyBike.route)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAccessItem(Icons.Default.WbSunny, "Weather", "Live conditions", modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.WeatherRoadConditions.route)
                }
                QuickAccessItem(Icons.Default.LocationOn, "Services", "Nearby shops", modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.NearbyRepairShops.route)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAccessItem(Icons.Default.Security, "Safety", "SOS & Tips", modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.Safety.route)
                }
                QuickAccessItem(Icons.Default.People, "Community", "Group rides", modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.GroupRides.route)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Weather Summary
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "WEATHER SUMMARY", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(text = "Demo only", color = primaryOrange, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CloudQueue, contentDescription = "Weather", tint = primaryOrange, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "24°", color = textWhite, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Pune · Demo weather · 26 Oct 09:00", color = textGrey, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(imageVector = Icons.Default.WarningAmber, contentDescription = "Warning", tint = primaryOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Extra caution advised", color = primaryOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Illustrative risk only. Never guarantees a safe road.", color = textGrey, fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Beyond the city
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Beyond the city", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Explore all", 
                    color = primaryOrange, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { navController.navigate(Screen.ScenicRoutes.route) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.moto_landscape),
                        contentDescription = "Landscape",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Dhom Lake, Wai", color = textWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Reservoir roads · Scenic stop on your route", color = textGrey, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "98 km", color = primaryOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Arrow", tint = textGrey)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pick up where you left off
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Pick up where you left off", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "View trips", 
                    color = primaryOrange, 
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
                    Text(text = "RECENT PLAN · 01 NOV 2026", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Pune -> Mahabaleshwar", color = textWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = "120 km", color = textWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text(text = "One-way", color = textGrey, fontSize = 12.sp)
                        }
                        Column {
                            Text(text = "₹420", color = textWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Estimated fuel", color = textGrey, fontSize = 12.sp)
                        }
                        Column {
                            Text(text = "3h 10m", color = textWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Demo duration", color = textGrey, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.navigate(Screen.TripPlanning.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = cardColorLighter),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.BookmarkBorder, contentDescription = "Continue", tint = textWhite, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Continue planning", color = textWhite, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun QuickAccessItem(icon: ImageVector, label: String, desc: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF18181B))
            .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF27272A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = Color(0xFFFF4500), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = label, color = Color(0xFFFAFAFA), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = desc, color = Color(0xFFA1A1AA), fontSize = 11.sp)
            }
        }
    }
}

data class CarouselSlide(
    val kicker: String,
    val title: String,
    val subtitle: String,
    val imageRes: Int
)

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MotoBuddyTheme {
        DashboardScreen(navController = rememberNavController())
    }
}
