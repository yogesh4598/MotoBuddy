package com.example.motobuddy.ui.screens.maintenance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    val maintenanceLogs = remember { mutableStateListOf<MaintenanceRecord>() }
    var showAddLogDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val remoteResult = repository.fetchMaintenanceRecordsRemote()
            maintenanceLogs.clear()
            maintenanceLogs.addAll(remoteResult.getOrDefault(repository.getMaintenanceRecords()))
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Service History Log", color = textWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddLogDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Log", tint = primaryCyan)
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
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Service Log")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("MAINTENANCE & REPAIR RECORDS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("${maintenanceLogs.size} Records", color = primaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                            maintenanceLogs.remove(log)
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

    // Add Maintenance Log Dialog
    if (showAddLogDialog) {
        var logTitle by remember { mutableStateOf("") }
        var logCategory by remember { mutableStateOf("Engine Oil") }
        var logNotes by remember { mutableStateOf("") }
        var logOdometer by remember { mutableStateOf("14250") }

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
                        placeholder = { Text("e.g. Engine Oil & Filter Replacement", color = textGrey) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                    OutlinedTextField(
                        value = logCategory,
                        onValueChange = { logCategory = it },
                        label = { Text("Category", color = textGrey) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite)
                    )
                    OutlinedTextField(
                        value = logOdometer,
                        onValueChange = { logOdometer = it },
                        label = { Text("Odometer Reading (km)", color = textGrey) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            val odo = logOdometer.toIntOrNull() ?: 14250
                            val newRecord = MaintenanceRecord(
                                title = logTitle,
                                category = logCategory,
                                date = "Today",
                                odometerKm = odo,
                                notes = logNotes
                            )
                            repository.addMaintenanceRecord(newRecord)
                            maintenanceLogs.add(0, newRecord)
                        }
                        showAddLogDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryCyan)
                ) {
                    Text("Add Record", color = MidnightNavy, fontWeight = FontWeight.Bold)
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
