package com.example.motobuddy.ui.screens.safety

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyScreen(navController: NavController) {
    val context = LocalContext.current

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    fun dialNumber(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    }

    fun launchHospitalSearch() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=hospital+emergency"))
        context.startActivity(intent)
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Safety & Emergency SOS", color = textWhite, fontWeight = FontWeight.Bold) },
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
            Spacer(modifier = Modifier.height(8.dp))

            // SOS Emergency Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1616)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, ErrorRed, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(ErrorRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "SOS", tint = textWhite, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("EMERGENCY ASSISTANCE", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("National Helpline / Medical SOS", color = textWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { dialNumber("112") },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = textWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Call National Emergency (112)", color = textWhite, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { dialNumber("108") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = textWhite),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Call Ambulance (108)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nearby Emergency Hospitals
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(imageVector = Icons.Default.LocalHospital, contentDescription = "Hospital", tint = primaryCyan, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Nearby Hospitals", color = textWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Locate emergency trauma centres", color = textGrey, fontSize = 12.sp)
                        }
                    }
                    Button(
                        onClick = { launchHospitalSearch() },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Locate", color = MidnightNavy, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pre-Ride Safety Checklist
            Text("PRE-RIDE SAFETY CHECKLIST", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SafetyCheckItem("Helmet & Visor: ECE/DOT certified, securely buckled.")
                    SafetyCheckItem("Tyre Pressure & Tread: Inspect cold pressure and sidewalls.")
                    SafetyCheckItem("Brakes & Clutch: Check lever resistance and fluid levels.")
                    SafetyCheckItem("Lights & Horn: Verify brake light and turn indicators.")
                    SafetyCheckItem("Chain Slack: Clean and lubricated with 25-35mm play.")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SafetyCheckItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.Security, contentDescription = "Check", tint = SuccessGreen, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = TextPrimary, fontSize = 12.sp, lineHeight = 16.sp)
    }
}
