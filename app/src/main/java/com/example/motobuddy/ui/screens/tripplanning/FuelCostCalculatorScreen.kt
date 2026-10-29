package com.example.motobuddy.ui.screens.tripplanning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelCostCalculatorScreen(navController: NavController) {
    val backgroundColor = Color(0xFF161618)
    val textWhite = Color(0xFFFFFFFF)
    val primaryOrange = Color(0xFFFF6600)
    val surfaceColor = Color(0xFF2A2A2A)

    var distance by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var fuelPrice by remember { mutableStateOf("") }

    val estimatedCost = remember(distance, mileage, fuelPrice) {
        val d = distance.toDoubleOrNull() ?: 0.0
        val m = mileage.toDoubleOrNull() ?: 0.0
        val p = fuelPrice.toDoubleOrNull() ?: 0.0
        if (m > 0) (d / m) * p else 0.0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fuel Calculator", color = textWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text("< Back", color = primaryOrange)
                    }
                }
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Calculate Your Trip Cost",
                color = textWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                label = { Text("Total Distance (km)", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryOrange,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                label = { Text("Bike Mileage (km/l)", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryOrange,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fuelPrice,
                onValueChange = { fuelPrice = it },
                label = { Text("Fuel Price (per liter)", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryOrange,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(surfaceColor, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Estimated Fuel Cost", color = Color.Gray, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format(Locale.US, "$%.2f", estimatedCost),
                        color = primaryOrange,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

