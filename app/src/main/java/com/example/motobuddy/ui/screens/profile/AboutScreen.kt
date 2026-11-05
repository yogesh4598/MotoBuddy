package com.example.motobuddy.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
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
                title = { Text("About MotoBuddy", color = textWhite, fontWeight = FontWeight.Bold) },
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
            Spacer(modifier = Modifier.height(16.dp))

            // App Icon Circle
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(cardColor)
                    .border(2.dp, primaryCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.DirectionsBike, contentDescription = "Logo", tint = secondaryOrange, modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("MotoBuddy", color = textWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Version 1.0 · Motorcycle Companion", color = primaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("TECH STACK & ARCHITECTURE", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("• Kotlin & Jetpack Compose UI", color = textWhite, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• MVVM Architecture + Retrofit 2", color = textWhite, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• OpenStreetMap & OSRM Routing Engine", color = textWhite, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• PHP REST API + MySQL Database", color = textWhite, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• AI Diagnostic Mechanic Engine", color = textWhite, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Built for passionate motorcyclists to plan rides, calculate fuel, track maintenance, and ride safely together.",
                color = textGrey,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
