package com.example.motobuddy.ui.screens.maintenance

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ChatbotScreen(navController: NavController) {
    // Delegates to full AI Diagnostic Assistant screen
    MechanicAssistantScreen(navController = navController)
}
