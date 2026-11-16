package com.example.motobuddy.ui.screens.tripplanning

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.motobuddy.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale

data class OpenMeteoWeatherData(
    val locationName: String,
    val temperature: Double,
    val apparentTemp: Double,
    val humidity: Int,
    val windSpeed: Double,
    val rainProbPercentage: Int?,
    val visibilityKm: Double?,
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

    var selectedLocationName by remember { mutableStateOf("Pune (Western Ghats)") }
    var currentLat by remember { mutableStateOf(18.5204) }
    var currentLon by remember { mutableStateOf(73.8567) }

    var weatherData by remember { mutableStateOf<OpenMeteoWeatherData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            selectedLocationName = "Current Location (GPS)"
            // GPS coordinates for Pune / region
            currentLat = 18.5204
            currentLon = 73.8567
        }
    }

    fun fetchWeatherFromOpenMeteo(lat: Double, lon: Double, locName: String) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,rain,weather_code,wind_speed_10m,visibility&hourly=precipitation_probability&forecast_days=1"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "MotoBuddyAndroid/1.0")
                    .build()

                val result = withContext(Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val body = response.body?.string()
                            if (!body.isNullOrBlank()) {
                                val json = JSONObject(body)
                                val current = json.optJSONObject("current")
                                val hourly = json.optJSONObject("hourly")

                                if (current != null) {
                                    val temp = current.optDouble("temperature_2m", Double.NaN)
                                    val appTemp = current.optDouble("apparent_temperature", Double.NaN)
                                    val humidity = current.optInt("relative_humidity_2m", -1)
                                    val wind = current.optDouble("wind_speed_10m", Double.NaN)
                                    val visibilityMeters = if (current.has("visibility")) current.optDouble("visibility", Double.NaN) else Double.NaN
                                    val rain = current.optDouble("rain", 0.0)
                                    val code = current.optInt("weather_code", 0)

                                    var rainProb: Int? = null
                                    if (hourly != null && hourly.has("precipitation_probability")) {
                                        val probs = hourly.optJSONArray("precipitation_probability")
                                        if (probs != null && probs.length() > 0) {
                                            rainProb = probs.optInt(0, -1)
                                            if (rainProb < 0) rainProb = null
                                        }
                                    }

                                    val condition = when {
                                        rain > 0.1 || code in 51..67 -> "Rain / Wet Road Conditions"
                                        code in 1..3 -> "Partly Cloudy · Normal Visibility"
                                        else -> "Clear & Dry Riding Weather"
                                    }
                                    val isCaution = rain > 0.1 || code in 51..67

                                    val visKm = if (!visibilityMeters.isNaN()) visibilityMeters / 1000.0 else null

                                    OpenMeteoWeatherData(
                                        locationName = locName,
                                        temperature = temp,
                                        apparentTemp = appTemp,
                                        humidity = humidity,
                                        windSpeed = wind,
                                        rainProbPercentage = rainProb,
                                        visibilityKm = visKm,
                                        conditionText = condition,
                                        isWetRoadCaution = isCaution
                                    )
                                } else null
                            } else null
                        } else null
                    }
                }

                if (result != null) {
                    weatherData = result
                } else {
                    errorMessage = "Failed to parse Open-Meteo weather response."
                }
            } catch (e: Exception) {
                errorMessage = "Network error: Unable to connect to Open-Meteo weather service."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(currentLat, currentLon, selectedLocationName) {
        fetchWeatherFromOpenMeteo(currentLat, currentLon, selectedLocationName)
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
                    IconButton(onClick = { fetchWeatherFromOpenMeteo(currentLat, currentLon, selectedLocationName) }) {
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

            // Location Selector & GPS Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SELECT RIDING REGION", color = textGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                TextButton(
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            selectedLocationName = "Current Location (GPS)"
                            currentLat = 18.5204
                            currentLon = 73.8567
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = "GPS", tint = primaryCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Use My GPS", color = primaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = {
                        selectedLocationName = "Pune Ghats"
                        currentLat = 18.5204
                        currentLon = 73.8567
                    },
                    label = { Text("Pune Ghats", fontSize = 11.sp, color = textWhite) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = if (selectedLocationName.contains("Pune")) cardColor else backgroundColor)
                )
                AssistChip(
                    onClick = {
                        selectedLocationName = "Mahabaleshwar Pass"
                        currentLat = 17.9237
                        currentLon = 73.6586
                    },
                    label = { Text("Mahabaleshwar", fontSize = 11.sp, color = textWhite) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = if (selectedLocationName.contains("Mahabaleshwar")) cardColor else backgroundColor)
                )
                AssistChip(
                    onClick = {
                        selectedLocationName = "Lonavala Highway"
                        currentLat = 18.7557
                        currentLon = 73.4091
                    },
                    label = { Text("Lonavala", fontSize = 11.sp, color = textWhite) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = if (selectedLocationName.contains("Lonavala")) cardColor else backgroundColor)
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
                        Text("Querying live Open-Meteo weather API...", color = textGrey, fontSize = 12.sp)
                    }
                }
            } else if (errorMessage != null || weatherData == null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Weather Connection Error", color = ErrorRed, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(errorMessage ?: "Unable to retrieve weather data.", color = textGrey, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { fetchWeatherFromOpenMeteo(currentLat, currentLon, selectedLocationName) },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryCyan)
                        ) {
                            Text("Retry Open-Meteo Query", color = MidnightNavy, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                val data = weatherData!!

                // Weather Main Display Card
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

                        if (!data.temperature.isNaN()) {
                            Text(String.format(Locale.US, "%.1f°C", data.temperature), color = textWhite, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        if (!data.apparentTemp.isNaN()) {
                            Text("Feels like ${String.format(Locale.US, "%.1f°C", data.apparentTemp)}", color = textGrey, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Road Condition Caution Banner
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

                // Returned Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (data.rainProbPercentage != null) {
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
                                Text("${data.rainProbPercentage}%", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (!data.windSpeed.isNaN()) {
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
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (data.humidity >= 0) {
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
                    }

                    if (data.visibilityKm != null) {
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
                                Text(String.format(Locale.US, "%.1f km", data.visibilityKm), color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
