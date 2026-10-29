package com.example.motobuddy.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.ui.screens.community.CommunityScreen
import com.example.motobuddy.ui.screens.maintenance.MechanicAssistantScreen
import com.example.motobuddy.ui.screens.profile.MyProfileScreen
import com.example.motobuddy.ui.screens.tripplanning.TripPlanningScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainHostScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = Color(0xFF09090B)
    val primaryOrange = Color(0xFFFF4500)
    val textWhite = Color(0xFFFAFAFA)
    val textGrey = Color(0xFFA1A1AA)

    Scaffold(
        bottomBar = {
            Surface(
                color = backgroundColor,
                shadowElevation = 8.dp
            ) {
                NavigationBar(
                    containerColor = backgroundColor,
                    contentColor = textWhite,
                    tonalElevation = 0.dp
                ) {
                NavigationBarItem(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryOrange,
                        selectedTextColor = primaryOrange,
                        unselectedIconColor = textGrey,
                        unselectedTextColor = textGrey,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                    label = { Text("Explore", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryOrange,
                        selectedTextColor = primaryOrange,
                        unselectedIconColor = textGrey,
                        unselectedTextColor = textGrey,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = pagerState.currentPage == 2,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                    icon = { Icon(Icons.Default.SmartToy, contentDescription = "Mechanic") },
                    label = { Text("Mechanic", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryOrange,
                        selectedTextColor = primaryOrange,
                        unselectedIconColor = textGrey,
                        unselectedTextColor = textGrey,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = pagerState.currentPage == 3,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(3) } },
                    icon = { Icon(Icons.AutoMirrored.Filled.AltRoute, contentDescription = "Trips") },
                    label = { Text("Trips", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryOrange,
                        selectedTextColor = primaryOrange,
                        unselectedIconColor = textGrey,
                        unselectedTextColor = textGrey,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = pagerState.currentPage == 4,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(4) } },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryOrange,
                        selectedTextColor = primaryOrange,
                        unselectedIconColor = textGrey,
                        unselectedTextColor = textGrey,
                        indicatorColor = Color.Transparent
                    )
                )
            }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Color(0xFF161618)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> DashboardScreen(navController)
                    1 -> TripPlanningScreen(navController)
                    2 -> MechanicAssistantScreen(navController)
                    3 -> CommunityScreen(navController)
                    4 -> MyProfileScreen(navController)
                }
            }
        }
    }
}
