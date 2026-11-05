package com.example.motobuddy.ui.screens.community

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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motobuddy.data.local.AuthManager
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.launch

data class SquadChatMessage(
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isUser: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderChatScreen(navController: NavController) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val currentUserName = remember { authManager.getUserName() ?: "You" }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val messages = remember {
        mutableStateListOf(
            SquadChatMessage("Aarav", "Hey squad! Meetup is at Chandani Chowk at 06:30 AM sharp.", "06:00 AM"),
            SquadChatMessage("Kabir", "Got it! Tire pressure checked, ready for the ghats.", "06:05 AM"),
            SquadChatMessage("Neha", "Bringing wet weather gear just in case. Rain probability is 40%.", "06:10 AM"),
            SquadChatMessage("Meera", "See everyone soon!", "06:12 AM")
        )
    }

    fun handleSend() {
        if (inputMessage.isBlank()) return
        val text = inputMessage.trim()
        messages.add(SquadChatMessage(senderName = currentUserName, text = text, timestamp = "Just now", isUser = true))
        inputMessage = ""
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
                        Text("Monsoon Squad Chat", color = textWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("5 Active Riders in Squad", color = primaryCyan, fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
                    }
                },
                actions = {
                    Icon(imageVector = Icons.Default.Group, contentDescription = "Squad", tint = textGrey, modifier = Modifier.padding(end = 16.dp))
                }
            )
        },
        bottomBar = {
            Surface(color = backgroundColor, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Message squad riders...", color = textGrey, fontSize = 13.sp) },
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
                        onClick = { handleSend() },
                        modifier = Modifier
                            .background(primaryCyan, CircleShape)
                            .size(48.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MidnightNavy)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(messages) { msg ->
                if (msg.isUser) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(msg.senderName, color = primaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                                .background(secondaryOrange.copy(alpha = 0.2f))
                                .border(1.dp, secondaryOrange, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                                .padding(12.dp)
                        ) {
                            Text(msg.text, color = textWhite, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(msg.timestamp, color = textGrey, fontSize = 9.sp)
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(msg.senderName, color = primaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp))
                                .background(cardColor)
                                .border(1.dp, DividerBorderColor, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp))
                                .padding(12.dp)
                        ) {
                            Text(msg.text, color = textWhite, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(msg.timestamp, color = textGrey, fontSize = 9.sp)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
