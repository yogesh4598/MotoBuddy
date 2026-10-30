package com.example.motobuddy.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.data.model.MaintenanceRecord
import com.example.motobuddy.data.repository.MotoRepository
import com.example.motobuddy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBikeScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }

    var motorcycle by remember { mutableStateOf(repository.getMotorcycle()) }
    var maintenanceLogs by remember { mutableStateOf(repository.getMaintenanceRecords()) }

    var showEditBikeDialog by remember { mutableStateOf(false) }
    var showAddLogDialog by remember { mutableStateOf(false) }

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
                title = { Text("Garage & Maintenance", color = textWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddLogDialog = true },
                containerColor = primaryCyan,
                contentColor = MidnightNavy
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Log")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Motorcycle Spec Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, primaryCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.DirectionsBike, contentDescription = "Bike", tint = secondaryOrange, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("MY MOTORCYCLE", color = textGrey, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            }
                            IconButton(onClick = { showEditBikeDialog = true }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = primaryCyan)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "${motorcycle.make} ${motorcycle.model}", color = textWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Model Year: ${motorcycle.year} · Owner: ${motorcycle.ownerName}", color = textGrey, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = DividerBorderColor)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Odometer Reading", color = textGrey, fontSize = 11.sp)
                                Text("${motorcycle.odometerKm} km", color = primaryCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Service Status", color = textGrey, fontSize = 11.sp)
                                Text("UP TO DATE", color = SuccessGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "SERVICE & REPAIR LOGS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(text = "${maintenanceLogs.size} Records", color = primaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            items(maintenanceLogs) { log ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkGraphite),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Build, contentDescription = "Log", tint = secondaryOrange, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = log.title, color = textWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${log.category} · ${log.date} · ${log.odometerKm} km", color = textGrey, fontSize = 12.sp)
                                if (log.notes.isNotBlank()) {
                                    Text(text = log.notes, color = primaryCyan, fontSize = 11.sp)
                                }
                            }
                        }
                        IconButton(onClick = {
                            repository.deleteMaintenanceRecord(log.id)
                            maintenanceLogs = repository.getMaintenanceRecords()
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    // Edit Bike Dialog
    if (showEditBikeDialog) {
        var makeInput by remember { mutableStateOf(motorcycle.make) }
        var modelInput by remember { mutableStateOf(motorcycle.model) }
        var odoInput by remember { mutableStateOf(motorcycle.odometerKm.toString()) }

        AlertDialog(
            onDismissRequest = { showEditBikeDialog = false },
            containerColor = cardColor,
            title = { Text("Update Motorcycle Details", color = textWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = makeInput,
                        onValueChange = { makeInput = it },
                        label = { Text("Make", color = textGrey) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                    OutlinedTextField(
                        value = modelInput,
                        onValueChange = { modelInput = it },
                        label = { Text("Model", color = textGrey) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                    OutlinedTextField(
                        value = odoInput,
                        onValueChange = { odoInput = it },
                        label = { Text("Odometer (km)", color = textGrey) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newOdo = odoInput.toIntOrNull() ?: motorcycle.odometerKm
                        val updated = motorcycle.copy(make = makeInput, model = modelInput, odometerKm = newOdo)
                        repository.saveMotorcycle(updated)
                        motorcycle = updated
                        showEditBikeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryCyan)
                ) {
                    Text("Save", color = MidnightNavy)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBikeDialog = false }) {
                    Text("Cancel", color = textGrey)
                }
            }
        )
    }

    // Add Maintenance Log Dialog
    if (showAddLogDialog) {
        var logTitle by remember { mutableStateOf("") }
        var logCategory by remember { mutableStateOf("Engine Oil") }
        var logNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddLogDialog = false },
            containerColor = cardColor,
            title = { Text("Record Service / Repair", color = textWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = logTitle,
                        onValueChange = { logTitle = it },
                        label = { Text("Service Description", color = textGrey) },
                        placeholder = { Text("e.g. Changed Brake Fluid", color = textGrey) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                    OutlinedTextField(
                        value = logCategory,
                        onValueChange = { logCategory = it },
                        label = { Text("Category", color = textGrey) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                    OutlinedTextField(
                        value = logNotes,
                        onValueChange = { logNotes = it },
                        label = { Text("Notes / Oil Grade", color = textGrey) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (logTitle.isNotBlank()) {
                            val newRecord = MaintenanceRecord(
                                title = logTitle,
                                category = logCategory,
                                date = "Today",
                                odometerKm = motorcycle.odometerKm,
                                notes = logNotes
                            )
                            repository.addMaintenanceRecord(newRecord)
                            maintenanceLogs = repository.getMaintenanceRecords()
                        }
                        showAddLogDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryCyan)
                ) {
                    Text("Add Record", color = MidnightNavy)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLogDialog = false }) {
                    Text("Cancel", color = textGrey)
                }
            }
        )
    }
}
