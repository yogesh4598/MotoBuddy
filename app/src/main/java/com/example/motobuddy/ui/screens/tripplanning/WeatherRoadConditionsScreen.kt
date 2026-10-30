package com.example.motobuddy.ui.screens.tripplanning

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.ui.theme.*

@Composable
fun WeatherRoadConditionsScreen(navController: NavController) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val textWhite = MaterialTheme.colorScheme.onBackground
    val primaryCyan = MaterialTheme.colorScheme.primary

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Weather & Road Conditions",
                color = textWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan)
            ) {
                Text("Go Back", color = MidnightNavy, fontWeight = FontWeight.Bold)
            }
        }
    }
}
