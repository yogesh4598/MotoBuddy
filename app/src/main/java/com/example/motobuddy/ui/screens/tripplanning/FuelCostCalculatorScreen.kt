package com.example.motobuddy.ui.screens.tripplanning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.data.model.FuelEntry
import com.example.motobuddy.data.repository.MotoRepository
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelCostCalculatorScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }
    val coroutineScope = rememberCoroutineScope()

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

    // Fuel Log State
    val fuelLogs = remember { mutableStateListOf<FuelEntry>() }
    var showAddFuelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val remoteResult = repository.fetchFuelEntriesRemote()
            fuelLogs.clear()
            fuelLogs.addAll(remoteResult.getOrDefault(repository.getFuelEntries()))
        }
    }

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

    val averageMileage = remember(fuelLogs) {
        repository.calculateAverageMileage(fuelLogs)
    }

    val totalSpent = remember(fuelLogs) {
        fuelLogs.sumOf { it.totalCost }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fuel Cost & Refuel Log", color = textWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddFuelDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Log", tint = primaryCyan)
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

            // Quick Refuel Stats Summary Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Speed, contentDescription = "Mileage", tint = primaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AVG MILEAGE", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(String.format(Locale.US, "%.1f km/L", averageMileage), color = textWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocalGasStation, contentDescription = "Spent", tint = secondaryOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TOTAL SPENT", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(String.format(Locale.US, "₹%.0f", totalSpent), color = secondaryOrange, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "TRIP FUEL ESTIMATOR",
                color = textGrey,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Inputs
            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                label = { Text("One-Way Distance (km)", color = textGrey) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryCyan,
                    unfocusedBorderColor = DividerBorderColor,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                label = { Text("Motorcycle Mileage (km/L)", color = textGrey) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryCyan,
                    unfocusedBorderColor = DividerBorderColor,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = fuelPrice,
                onValueChange = { fuelPrice = it },
                label = { Text("Fuel Price (per Liter in ₹)", color = textGrey) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryCyan,
                    unfocusedBorderColor = DividerBorderColor,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

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
                        Text("ESTIMATED TRIP EXPENSE", color = textGrey, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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
                    HorizontalDivider(color = DividerBorderColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Total Fuel Expense", color = textGrey, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "₹%.2f", estimatedCost),
                        color = secondaryOrange,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Fuel History Log List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("REFUEL LOG HISTORY", color = textGrey, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                TextButton(onClick = { showAddFuelDialog = true }) {
                    Text("+ Add Refuel Log", color = primaryCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (fuelLogs.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "No refuel logs recorded yet. Tap '+ Add Refuel Log' to track fuel entries.",
                        color = textGrey,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    fuelLogs.forEach { entry ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${entry.liters} Liters @ ₹${entry.pricePerLiter}/L", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Odometer: ${entry.odometerKm} km · ${entry.date}", color = textGrey, fontSize = 12.sp)
                                    if (entry.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(entry.notes, color = primaryCyan, fontSize = 11.sp)
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("₹${entry.totalCost.toInt()}", color = secondaryOrange, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            repository.deleteFuelEntry(entry.id)
                                            fuelLogs.remove(entry)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = textGrey, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Add Fuel Log Dialog
    if (showAddFuelDialog) {
        var logOdometer by remember { mutableStateOf("14500") }
        var logLiters by remember { mutableStateOf("10") }
        var logPrice by remember { mutableStateOf("104.5") }
        var logNotes by remember { mutableStateOf("Full tank") }

        AlertDialog(
            onDismissRequest = { showAddFuelDialog = false },
            containerColor = cardColor,
            title = { Text("Log Refuel Entry", color = textWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = logOdometer,
                        onValueChange = { logOdometer = it },
                        label = { Text("Odometer (km)", color = textGrey) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryCyan, unfocusedBorderColor = DividerBorderColor, focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                    OutlinedTextField(
                        value = logLiters,
                        onValueChange = { logLiters = it },
                        label = { Text("Liters Refueled", color = textGrey) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryCyan, unfocusedBorderColor = DividerBorderColor, focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                    OutlinedTextField(
                        value = logPrice,
                        onValueChange = { logPrice = it },
                        label = { Text("Price per Liter (₹)", color = textGrey) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryCyan, unfocusedBorderColor = DividerBorderColor, focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                    OutlinedTextField(
                        value = logNotes,
                        onValueChange = { logNotes = it },
                        label = { Text("Notes / Location", color = textGrey) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryCyan, unfocusedBorderColor = DividerBorderColor, focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val odo = logOdometer.toIntOrNull() ?: 14500
                        val lit = logLiters.toDoubleOrNull() ?: 10.0
                        val pr = logPrice.toDoubleOrNull() ?: 104.5
                        val total = lit * pr
                        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date())

                        val entry = FuelEntry(
                            date = dateStr,
                            odometerKm = odo,
                            liters = lit,
                            pricePerLiter = pr,
                            totalCost = total,
                            notes = logNotes
                        )
                        repository.addFuelEntry(entry)
                        fuelLogs.add(0, entry)
                        showAddFuelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryCyan)
                ) {
                    Text("Save Log", color = MidnightNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFuelDialog = false }) {
                    Text("Cancel", color = textGrey)
                }
            }
        )
    }
}
