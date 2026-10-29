package com.example.motobuddy.ui.screens.tripplanning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelCostCalculatorScreen(navController: NavController) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var distance by remember { mutableStateOf("120") }
    var mileage by remember { mutableStateOf("35") }
    var fuelPrice by remember { mutableStateOf("104.5") }
    var isRoundTrip by remember { mutableStateOf(false) }

    val effectiveDistance = remember(distance, isRoundTrip) {
        val d = distance.toDoubleOrNull() ?: 0.0
        if (isRoundTrip) d * 2 else d
    }

    val fuelLitersRequired = remember(effectiveDistance, mileage) {
        val m = mileage.toDoubleOrNull() ?: 0.0
        if (m > 0) effectiveDistance / m else 0.0
    }

    val estimatedCost = remember(fuelLitersRequired, fuelPrice) {
        val p = fuelPrice.toDoubleOrNull() ?: 0.0
        fuelLitersRequired * p
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fuel Cost Calculator", color = textWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Calculate exact fuel requirements and cost for your upcoming motorcycle trip.",
                color = textGrey,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Inputs
            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                label = { Text("One-Way Distance (km)", color = textGrey) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryCyan,
                    unfocusedBorderColor = Color(0xFF27272A),
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                label = { Text("Motorcycle Mileage (km/L)", color = textGrey) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryCyan,
                    unfocusedBorderColor = Color(0xFF27272A),
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fuelPrice,
                onValueChange = { fuelPrice = it },
                label = { Text("Fuel Price (per Liter in ₹)", color = textGrey) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryCyan,
                    unfocusedBorderColor = Color(0xFF27272A),
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Round trip Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardColor)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Round Trip (Return)", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Doubles the effective distance", color = textGrey, fontSize = 11.sp)
                }
                Switch(
                    checked = isRoundTrip,
                    onCheckedChange = { isRoundTrip = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = MidnightNavy, checkedTrackColor = primaryCyan)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Result Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, primaryCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocalGasStation, contentDescription = "Fuel", tint = secondaryOrange, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ESTIMATED FUEL BREAKDOWN", color = textGrey, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Distance", color = textGrey, fontSize = 11.sp)
                            Text("${effectiveDistance.toInt()} km", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Liters Needed", color = textGrey, fontSize = 11.sp)
                            Text(String.format(Locale.US, "%.1f L", fuelLitersRequired), color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFF27272A))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Total Fuel Expense", color = textGrey, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "₹%.2f", estimatedCost),
                        color = secondaryOrange,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
