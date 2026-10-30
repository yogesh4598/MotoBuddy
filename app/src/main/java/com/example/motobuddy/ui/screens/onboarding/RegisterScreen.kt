package com.example.motobuddy.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.state.UiState
import com.example.motobuddy.ui.theme.*
import com.example.motobuddy.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        if (registerState is UiState.Success) {
            navController.navigate(Screen.MainHost.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        }
    }

    Scaffold(containerColor = backgroundColor) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Register Account",
                color = textWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Create your rider profile",
                color = textGrey,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name", color = textGrey) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = primaryCyan) },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite, focusedBorderColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address", color = textGrey) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = primaryCyan) },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite, focusedBorderColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = textGrey) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = primaryCyan) },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textWhite, unfocusedTextColor = textWhite, focusedBorderColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (registerState is UiState.Error) {
                Text(
                    text = (registerState as UiState.Error).message,
                    color = ErrorRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { viewModel.register(name, email, password) },
                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                shape = RoundedCornerShape(12.dp),
                enabled = registerState !is UiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (registerState is UiState.Loading) {
                    CircularProgressIndicator(color = MidnightNavy, modifier = Modifier.size(24.dp))
                } else {
                    Text("Register Profile", color = MidnightNavy, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Already registered? Login here", color = secondaryOrange)
            }
        }
    }
}
