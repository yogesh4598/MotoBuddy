package com.example.motobuddy.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.mapping.OsmRouteProvider
import com.example.motobuddy.mapping.PlaceSearchResult
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<PlaceSearchResult>() }
    var isSearching by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun performSearch(query: String) {
        if (query.isBlank()) return
        coroutineScope.launch {
            isSearching = true
            val results = OsmRouteProvider.searchNearbyPlaces(query, "Pune")
            searchResults.clear()
            searchResults.addAll(results)
            isSearching = false
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Search Routes & Services", color = textWhite, fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search Input Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    performSearch(it)
                },
                placeholder = { Text("Search petrol pumps, repair shops, mountain passes...", color = textGrey, fontSize = 13.sp) },
                trailingIcon = {
                    IconButton(onClick = { performSearch(searchQuery) }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = primaryCyan)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryCyan,
                    unfocusedBorderColor = cardColor,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isSearching) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryCyan)
                }
            } else if (searchResults.isEmpty()) {
                // Quick Suggestion Chips
                Text("POPULAR QUICK SEARCHES", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {
                            searchQuery = "Petrol Pump"
                            performSearch("Petrol Pump")
                        },
                        label = { Text("Petrol Pumps", fontSize = 12.sp, color = textWhite) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = cardColor)
                    )
                    AssistChip(
                        onClick = {
                            searchQuery = "Motorcycle Repair"
                            performSearch("Motorcycle Repair")
                        },
                        label = { Text("Repair Shops", fontSize = 12.sp, color = textWhite) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = cardColor)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {
                            searchQuery = "Mahabaleshwar"
                            performSearch("Mahabaleshwar")
                        },
                        label = { Text("Scenic Routes", fontSize = 12.sp, color = primaryCyan) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = cardColor)
                    )
                    AssistChip(
                        onClick = {
                            searchQuery = "Hospital"
                            performSearch("Hospital")
                        },
                        label = { Text("Emergency Hospitals", fontSize = 12.sp, color = ErrorRed) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = cardColor)
                    )
                }
            } else {
                Text("SEARCH RESULTS (${searchResults.size})", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(searchResults) { result ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DividerBorderColor, RoundedCornerShape(12.dp))
                                .clickable { navController.navigate(Screen.TripPlanning.route) }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Place", tint = secondaryOrange, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(result.name, color = textWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(result.address, color = textGrey, fontSize = 11.sp, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
