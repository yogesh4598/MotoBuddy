package com.example.motobuddy.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import com.example.motobuddy.data.model.Motorcycle
import com.example.motobuddy.data.repository.MotoRepository
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileSetupScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var makeInput by remember { mutableStateOf("Royal Enfield") }
    var modelInput by remember { mutableStateOf("Himalayan 411") }
    var yearInput by remember { mutableStateOf("2022") }
    var odoInput by remember { mutableStateOf("14250") }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Initial Motorcycle Setup", color = textWhite, fontWeight = FontWeight.Bold) },
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

            Text("Setup Your Primary Motorcycle", color = textWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Enter your bike details so MotoBuddy can calculate fuel costs, maintenance reminders, and diagnostic tips.", color = textGrey, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = makeInput,
                onValueChange = { makeInput = it },
                label = { Text("Manufacturer / Make", color = textGrey) },
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
                value = modelInput,
                onValueChange = { modelInput = it },
                label = { Text("Model Name", color = textGrey) },
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
                value = yearInput,
                onValueChange = { yearInput = it },
                label = { Text("Model Year", color = textGrey) },
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
                value = odoInput,
                onValueChange = { odoInput = it },
                label = { Text("Current Odometer Reading (km)", color = textGrey) },
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

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val year = yearInput.toIntOrNull() ?: 2022
                    val odo = odoInput.toIntOrNull() ?: 14250
                    val moto = Motorcycle(
                        make = if (makeInput.isNotBlank()) makeInput.trim() else "Royal Enfield",
                        model = if (modelInput.isNotBlank()) modelInput.trim() else "Himalayan 411",
                        year = year,
                        odometerKm = odo
                    )
                    repository.saveMotorcycle(moto)
                    navController.navigate(Screen.MainHost.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = MidnightNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Complete Setup & Enter App", color = MidnightNavy, fontWeight = FontWeight.Bold)
            }
        }
    }
}
