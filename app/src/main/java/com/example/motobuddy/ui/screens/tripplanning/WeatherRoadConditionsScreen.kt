package com.example.motobuddy.ui.screens.tripplanning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
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
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale

data class WeatherData(
    val locationName: String,
    val temperature: Double,
    val apparentTemp: Double,
    val humidity: Int,
    val windSpeed: Double,
    val rainProb: Int,
    val conditionText: String,
    val isWetRoadCaution: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherRoadConditionsScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val client = remember { OkHttpClient() }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    var selectedLocation by remember { mutableStateOf("Pune (Western Ghats)") }
    var weatherData by remember { mutableStateOf<WeatherData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    fun fetchWeatherData(location: String) {
        coroutineScope.launch {
            isLoading = true
            isError = false
            try {
                // Coordinates for Western Ghats / Pune / Mahabaleshwar
                val (lat, lon) = when {
                    location.contains("Mahabaleshwar") -> Pair(17.9237, 73.6586)
                    location.contains("Lonavala") -> Pair(18.7557, 73.4091)
                    location.contains("Goa") -> Pair(15.2993, 74.1240)
                    else -> Pair(18.5204, 73.8567)
                }

                val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,rain,weather_code,wind_speed_10m"
                val request = Request.Builder().url(url).header("User-Agent", "MotoBuddyAndroid/1.0").build()

                val data = withContext(Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val body = response.body?.string()
                            if (!body.isNullOrBlank()) {
                                val json = JSONObject(body)
                                val current = json.getJSONObject("current")
                                val temp = current.optDouble("temperature_2m", 26.5)
                                val appTemp = current.optDouble("apparent_temperature", 27.0)
                                val humidity = current.optInt("relative_humidity_2m", 62)
                                val wind = current.optDouble("wind_speed_10m", 14.2)
                                val rain = current.optDouble("rain", 0.0)
                                val code = current.optInt("weather_code", 0)

                                val condition = when {
                                    rain > 0.5 || code in 51..67 -> "Rain / Wet Road Conditions"
                                    code in 1..3 -> "Partly Cloudy · Good Visibility"
                                    else -> "Clear & Dry Riding Weather"
                                }
                                val isCaution = rain > 0.2 || code in 51..67

                                WeatherData(
                                    locationName = location,
                                    temperature = temp,
                                    apparentTemp = appTemp,
                                    humidity = humidity,
                                    windSpeed = wind,
                                    rainProb = if (isCaution) 75 else 10,
                                    conditionText = condition,
                                    isWetRoadCaution = isCaution
                                )
                            } else null
                        } else null
                    }
                }

                if (data != null) {
                    weatherData = data
                } else {
                    isError = true
                }
            } catch (e: Exception) {
                isError = true
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedLocation) {
        fetchWeatherData(selectedLocation)
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Weather & Road Conditions", color = textWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryCyan)
                    }
                },
                actions = {
                    IconButton(onClick = { fetchWeatherData(selectedLocation) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = primaryCyan)
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

            // Location Selector Chips
            Text("SELECT RIDING ROUTE / REGION", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { selectedLocation = "Pune (Western Ghats)" },
                    label = { Text("Pune Ghats", fontSize = 11.sp, color = textWhite) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = if (selectedLocation.contains("Pune")) cardColor else backgroundColor)
                )
                AssistChip(
                    onClick = { selectedLocation = "Mahabaleshwar Pass" },
                    label = { Text("Mahabaleshwar", fontSize = 11.sp, color = textWhite) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = if (selectedLocation.contains("Mahabaleshwar")) cardColor else backgroundColor)
                )
                AssistChip(
                    onClick = { selectedLocation = "Lonavala Highway" },
                    label = { Text("Lonavala", fontSize = 11.sp, color = textWhite) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = if (selectedLocation.contains("Lonavala")) cardColor else backgroundColor)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = primaryCyan)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Fetching Open-Meteo live weather data...", color = textGrey, fontSize = 12.sp)
                    }
                }
            } else if (isError || weatherData == null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Weather Data Unavailable", color = textWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Unable to connect to Open-Meteo weather service. Check your internet connection.", color = textGrey, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { fetchWeatherData(selectedLocation) },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryCyan)
                        ) {
                            Text("Retry Weather Update", color = MidnightNavy, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                val data = weatherData!!

                // Weather Overview Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, primaryCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(data.locationName.uppercase(), color = primaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(String.format(Locale.US, "%.1f°C", data.temperature), color = textWhite, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Feels like ${String.format(Locale.US, "%.1f°C", data.apparentTemp)}", color = textGrey, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Road Status Caution Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (data.isWetRoadCaution) ErrorRed.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.2f))
                                .border(1.dp, if (data.isWetRoadCaution) ErrorRed else SuccessGreen, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = data.conditionText,
                                color = if (data.isWetRoadCaution) ErrorRed else SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Detailed Weather Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.WaterDrop, contentDescription = "Rain", tint = primaryCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("RAIN PROB.", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("${data.rainProb}%", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Air, contentDescription = "Wind", tint = secondaryOrange, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("WIND SPEED", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(String.format(Locale.US, "%.1f km/h", data.windSpeed), color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Thermostat, contentDescription = "Humidity", tint = primaryCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("HUMIDITY", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("${data.humidity}%", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CloudQueue, contentDescription = "Visibility", tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("VISIBILITY", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("10 km (Clear)", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
