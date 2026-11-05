package com.example.motobuddy.ui.screens.safety

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencySOSScreen(navController: NavController) {
    val context = LocalContext.current

    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryCyan = MaterialTheme.colorScheme.primary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var isSosTriggered by remember { mutableStateOf(false) }

    fun triggerEmergencyCall() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
        context.startActivity(intent)
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Emergency SOS Beacon", color = textWhite, fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("EMERGENCY RESPONSE", color = ErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tap the SOS beacon to alert emergency contacts and national dispatchers.", color = textGrey, fontSize = 13.sp, textAlign = TextAlign.Center)
            }

            // Giant SOS Button
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(if (isSosTriggered) ErrorRed else Color(0xFF3D1414))
                    .border(4.dp, ErrorRed, CircleShape)
                    .clickable {
                        isSosTriggered = true
                        triggerEmergencyCall()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "SOS", tint = textWhite, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("SOS", color = textWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("TAP FOR HELP", color = textWhite.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                if (isSosTriggered) {
                    Text("✓ SOS Beacon Activated - Dialing Emergency Services (112)", color = ErrorRed, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = { triggerEmergencyCall() },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Call 112", tint = textWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Direct Call 112 Dispatch", color = textWhite, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
