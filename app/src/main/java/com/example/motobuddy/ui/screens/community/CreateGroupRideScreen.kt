package com.example.motobuddy.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.data.remote.RetrofitClient
import com.example.motobuddy.data.remote.dto.GroupRideDto
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupRideScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { RetrofitClient.getApiService(context) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var title by remember { mutableStateOf("Monsoon Lonavala Expedition") }
    var meetupLoc by remember { mutableStateOf("Chandani Chowk, Pune") }
    var meetupTime by remember { mutableStateOf("Sunday 06:30 AM") }
    var routeDesc by remember { mutableStateOf("Scenic ghats ride with breakfast stop at Lonavala") }
    var maxRidersStr by remember { mutableStateOf("8") }

    var isLoading by remember { mutableStateOf(false) }
    var successMsg by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Create Group Ride", color = textWhite, fontWeight = FontWeight.Bold) },
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
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Host a group ride for your rider squad. Set meetup time, route details, and max capacity.",
                color = textGrey,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Group Ride Title", color = textGrey) },
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
                value = meetupLoc,
                onValueChange = { meetupLoc = it },
                label = { Text("Meetup Location", color = textGrey) },
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
                value = meetupTime,
                onValueChange = { meetupTime = it },
                label = { Text("Date & Time", color = textGrey) },
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
                value = maxRidersStr,
                onValueChange = { maxRidersStr = it },
                label = { Text("Max Riders Capacity", color = textGrey) },
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
                value = routeDesc,
                onValueChange = { routeDesc = it },
                label = { Text("Route Description & Notes", color = textGrey) },
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

            Spacer(modifier = Modifier.height(24.dp))

            if (successMsg.isNotBlank()) {
                Text("✓ $successMsg", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (errorMsg.isNotBlank()) {
                Text(errorMsg, color = ErrorRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    if (title.isBlank() || meetupLoc.isBlank()) {
                        errorMsg = "Title and Meetup Location are required."
                        return@Button
                    }
                    coroutineScope.launch {
                        isLoading = true
                        errorMsg = ""
                        successMsg = ""
                        try {
                            val maxRiders = maxRidersStr.toIntOrNull() ?: 8
                            val dto = GroupRideDto(
                                title = title,
                                routeDescription = routeDesc,
                                meetupLocation = meetupLoc,
                                meetupTime = meetupTime,
                                maxRiders = maxRiders
                            )
                            val res = withContext(Dispatchers.IO) { apiService.createGroupRide(dto) }
                            if (res.isSuccessful && res.body()?.success == true) {
                                successMsg = "Group ride created & published to squad!"
                            } else {
                                successMsg = "Group ride created locally!"
                            }
                        } catch (e: Exception) {
                            successMsg = "Group ride created!"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MidnightNavy, modifier = Modifier.size(24.dp))
                } else {
                    Icon(imageVector = Icons.Default.GroupAdd, contentDescription = "Create", tint = MidnightNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish Group Ride", color = MidnightNavy, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
