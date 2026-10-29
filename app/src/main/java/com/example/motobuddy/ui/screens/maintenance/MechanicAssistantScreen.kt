package com.example.motobuddy.ui.screens.maintenance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.data.repository.MotoRepository
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER" or "AI"
    val text: String,
    val isWarning: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicAssistantScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { MotoRepository(context) }
    val motorcycle = remember { repository.getMotorcycle() }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Chat History
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "AI",
                text = "Hello! I am your MotoBuddy Offline AI Mechanic. I can help troubleshoot starting issues, battery concerns, brake noise, or chain maintenance for your ${motorcycle.make} ${motorcycle.model}.\n\nHow is your bike behaving?"
            )
        )
    }

    fun handleUserSendMessage(userText: String) {
        if (userText.isBlank()) return
        val trimmed = userText.trim()
        messages.add(ChatMessage(sender = "USER", text = trimmed))
        inputMessage = ""

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
        }

        // Generate Offline AI Assistant Response
        val lower = trimmed.lowercase()
        val aiResponse: ChatMessage = when {
            lower.contains("brake") || lower.contains("stop") -> {
                ChatMessage(
                    sender = "AI",
                    text = "⚠️ SAFETY WARNING: Brake issues are critical!\n\n1. If your brake lever feels spongy, check the brake fluid reservoir level.\n2. Squealing noises usually indicate worn brake pads.\n3. Do NOT ride if braking power is reduced. Inspect pads immediately or visit a service shop.",
                    isWarning = true
                )
            }
            lower.contains("start") || lower.contains("click") || lower.contains("battery") || lower.contains("dead") -> {
                ChatMessage(
                    sender = "AI",
                    text = "Starting Issue Diagnostics:\n\n1. Rapid clicking sound when pressing ignition usually means low battery voltage.\n2. Ensure the kill switch is set to RUN and side stand is UP in gear.\n3. Check terminal connections for corrosion.\n4. If headlights are dim, jump-start or charge the 12V battery."
                )
            }
            lower.contains("heat") || lower.contains("hot") || lower.contains("coolant") || lower.contains("smoke") -> {
                ChatMessage(
                    sender = "AI",
                    text = "⚠️ OVERHEATING ALERT:\n\n1. Immediately pull over safely and switch off the engine!\n2. Check if radiator fan is running (if liquid cooled).\n3. Allow engine to cool 20+ minutes before opening coolant/oil caps.\n4. Check for oil or coolant leaks underneath.",
                    isWarning = true
                )
            }
            lower.contains("chain") || lower.contains("slack") || lower.contains("noise") -> {
                ChatMessage(
                    sender = "AI",
                    text = "Drive Chain Advice:\n\n1. Ideal chain slack is 25-35 mm.\n2. Clean with O-ring safe chain cleaner every 500 km and lube with EP lube.\n3. Rattling or clicking noises while rolling indicate uneven chain stretch or worn sprockets."
                )
            }
            lower.contains("oil") || lower.contains("filter") -> {
                ChatMessage(
                    sender = "AI",
                    text = "Engine Oil Guidance:\n\nFor your ${motorcycle.model}, check oil level using the sight glass/dipstick with the bike held upright on level ground. Top up with manufacturer-recommended grade (e.g. 15W-50 Semi/Full Synthetic) if below the MIN line."
                )
            }
            else -> {
                ChatMessage(
                    sender = "AI",
                    text = "I received your note about '${trimmed}'.\n\nTo help narrow this down: Is the issue related to starting, electrical/battery, unusual engine noise, or braking? Feel free to tap one of the suggested diagnostic chips below!"
                )
            }
        }

        messages.add(aiResponse)
        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Mechanic Assistant", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Offline Diagnostic Engine · ${motorcycle.model}", color = primaryCyan, fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        messages.clear()
                        messages.add(
                            ChatMessage(
                                sender = "AI",
                                text = "Chat cleared. What motorcycle symptom should we diagnose?"
                            )
                        )
                    }) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear", tint = textGrey)
                    }
                }
            )
        },
        bottomBar = {
            Surface(color = backgroundColor, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).navigationBarsPadding()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = inputMessage,
                            onValueChange = { inputMessage = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Ask about starting, battery, noises...", color = textGrey, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryCyan,
                                unfocusedBorderColor = cardColor,
                                focusedTextColor = textWhite,
                                unfocusedTextColor = textWhite,
                                focusedContainerColor = cardColor,
                                unfocusedContainerColor = cardColor
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { handleUserSendMessage(inputMessage) },
                            modifier = Modifier
                                .background(primaryCyan, CircleShape)
                                .size(48.dp)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MidnightNavy)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Quick Symptom Chips
            Text("QUICK DIAGNOSTIC SYMPTOMS", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { handleUserSendMessage("Bike won't start clicking sound") },
                    label = { Text("Won't Start / Clicking", fontSize = 11.sp, color = textWhite) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = cardColor)
                )
                AssistChip(
                    onClick = { handleUserSendMessage("Brake noise spongy lever") },
                    label = { Text("Brake Noise", fontSize = 11.sp, color = textWhite) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = cardColor)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { handleUserSendMessage("Engine overheating warning") },
                    label = { Text("Engine Overheating", fontSize = 11.sp, color = ErrorRed) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = cardColor)
                )
                AssistChip(
                    onClick = { handleUserSendMessage("Chain slack lube") },
                    label = { Text("Chain Maintenance", fontSize = 11.sp, color = textWhite) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = cardColor)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    if (msg.sender == "USER") {
                        // User bubble
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                                    .background(secondaryOrange.copy(alpha = 0.2f))
                                    .border(1.dp, secondaryOrange, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                                    .padding(14.dp)
                            ) {
                                Text(text = msg.text, color = textWhite, fontSize = 14.sp)
                            }
                        }
                    } else {
                        // AI bubble
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.SmartToy, contentDescription = "AI", tint = primaryCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("MOTOBUDDY AI MECHANIC", color = primaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp))
                                    .background(if (msg.isWarning) Color(0xFF2D1616) else cardColor)
                                    .border(1.dp, if (msg.isWarning) ErrorRed else Color(0xFF27272A), RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (msg.isWarning) ErrorRed else textWhite,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
