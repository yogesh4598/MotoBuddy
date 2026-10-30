package com.example.motobuddy.ui.screens.profile

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.motobuddy.data.local.AuthManager
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.state.UiState
import com.example.motobuddy.ui.theme.*
import com.example.motobuddy.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun MyProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val sharedPreferences = remember { context.getSharedPreferences("MotoBuddyPrefs", Context.MODE_PRIVATE) }
    
    val userEmail = remember { authManager.getUserEmail() ?: "aarav.sharma@demo.com" }
    val userName = remember { userEmail.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() } }

    var showLogoutDialog by remember { mutableStateOf(false) }

    val logoutState by authViewModel.logoutState.collectAsState()

    LaunchedEffect(logoutState) {
        if (logoutState is UiState.Success) {
            authViewModel.resetLogoutState()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    var profileImageUri by remember { 
        val savedUri = sharedPreferences.getString("profile_image_uri", null)
        mutableStateOf(savedUri?.let { Uri.parse(it) })
    }

    val coroutineScope = rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val file = File(context.filesDir, "profile_pic.jpg")
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()
                    
                    val localUri = Uri.fromFile(file)
                    withContext(Dispatchers.Main) {
                        profileImageUri = localUri
                        sharedPreferences.edit().putString("profile_image_uri", localUri.toString()).apply()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PROFILE",
                    color = textWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(cardColor)
                    .border(2.dp, primaryCyan, CircleShape)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                val bitmap = rememberBitmapFromUri(uri = profileImageUri)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Avatar",
                        tint = primaryCyan,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            TextButton(onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }) {
                Text(text = "Change Profile Photo", color = primaryCyan, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = userName, color = textWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = userEmail, color = textGrey, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Items
            ProfileMenuItem(
                icon = Icons.Default.DirectionsBike,
                title = "My Motorcycles",
                subtitle = "Manage your bikes",
                iconTint = secondaryOrange,
                onClick = { navController.navigate(Screen.MyBike.route) }
            )
            ProfileMenuItem(
                icon = Icons.Default.History,
                title = "Ride History",
                subtitle = "View past trips",
                iconTint = primaryCyan,
                onClick = { navController.navigate(Screen.TripHistory.route) }
            )
            ProfileMenuItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                subtitle = "App preferences",
                iconTint = primaryCyan,
                onClick = { navController.navigate(Screen.Settings.route) }
            )
            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = "About MotoBuddy",
                subtitle = "App version 1.0",
                iconTint = primaryCyan,
                onClick = { navController.navigate(Screen.About.route) }
            )

            // Prominent Logout Option
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Log Out",
                subtitle = "Sign out of your MotoBuddy account",
                iconTint = ErrorRed,
                titleColor = ErrorRed,
                onClick = { showLogoutDialog = true },
                showDivider = false
            )
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // Confirmation Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = cardColor,
            title = { Text(text = "Log out?", color = textWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Are you sure you want to log out of your MotoBuddy account?",
                    color = textGrey,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.performLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    if (logoutState is UiState.Loading) {
                        CircularProgressIndicator(color = textWhite, modifier = Modifier.size(18.dp))
                    } else {
                        Text(text = "Log Out", color = textWhite, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = "Cancel", color = textGrey)
                }
            }
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: Color = VibrantOrange,
    titleColor: Color = TextPrimary,
    showDivider: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkGraphite),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconTint)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = titleColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = TextSecondary, fontSize = 12.sp)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Go", tint = TextSecondary)
        }
        if (showDivider) {
            HorizontalDivider(color = DividerBorderColor, thickness = 1.dp)
        }
    }
}

@Composable
fun rememberBitmapFromUri(uri: Uri?): ImageBitmap? {
    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }
    
    LaunchedEffect(uri) {
        if (uri != null) {
            withContext(Dispatchers.IO) {
                try {
                    val resolvedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }
                    bitmap = resolvedBitmap.asImageBitmap()
                } catch (e: Exception) {
                    e.printStackTrace()
                    bitmap = null
                }
            }
        } else {
            bitmap = null
        }
    }
    return bitmap
}
