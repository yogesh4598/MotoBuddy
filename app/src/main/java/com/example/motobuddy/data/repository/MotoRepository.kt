package com.example.motobuddy.data.repository

import android.content.Context
import com.example.motobuddy.data.model.MaintenanceRecord
import com.example.motobuddy.data.model.Motorcycle
import com.example.motobuddy.data.model.Trip
import com.example.motobuddy.data.remote.RetrofitClient
import com.example.motobuddy.data.remote.dto.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class MotoRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("motobuddy_local_store", Context.MODE_PRIVATE)
    private val apiService = RetrofitClient.getApiService(context)

    fun clearUserData() {
        prefs.edit().clear().apply()
    }

    // Motorcycle details
    suspend fun fetchMotorcyclesRemote(): Result<List<Motorcycle>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMotorcycles()
            if (response.isSuccessful && response.body()?.success == true) {
                val dtos = response.body()?.data ?: emptyList()
                val list = dtos.map { dto ->
                    Motorcycle(
                        make = dto.make,
                        model = dto.model,
                        year = dto.year,
                        odometerKm = dto.odometerKm,
                        ownerName = dto.ownerName ?: "Rider"
                    )
                }
                if (list.isNotEmpty()) {
                    saveMotorcycle(list.first())
                }
                Result.success(list)
            } else {
                Result.success(listOf(getMotorcycle()))
            }
        } catch (e: Exception) {
            Result.success(listOf(getMotorcycle()))
        }
    }

    fun getMotorcycle(): Motorcycle {
        val make = prefs.getString("moto_make", "Royal Enfield") ?: "Royal Enfield"
        val model = prefs.getString("moto_model", "Himalayan 411") ?: "Himalayan 411"
        val year = prefs.getInt("moto_year", 2022)
        val odo = prefs.getInt("moto_odo", 14250)
        val owner = prefs.getString("moto_owner", "Aarav Sharma") ?: "Aarav Sharma"
        return Motorcycle(make, model, year, odo, owner)
    }

    fun saveMotorcycle(moto: Motorcycle) {
        prefs.edit()
            .putString("moto_make", moto.make)
            .putString("moto_model", moto.model)
            .putInt("moto_year", moto.year)
            .putInt("moto_odo", moto.odometerKm)
            .putString("moto_owner", moto.ownerName)
            .apply()
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService.addMotorcycle(
                    MotorcycleDto(
                        make = moto.make,
                        model = moto.model,
                        year = moto.year,
                        odometerKm = moto.odometerKm,
                        ownerName = moto.ownerName
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Maintenance Records
    suspend fun fetchMaintenanceRecordsRemote(): Result<List<MaintenanceRecord>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMaintenanceRecords()
            if (response.isSuccessful && response.body()?.success == true) {
                val dtos = response.body()?.data ?: emptyList()
                val list = dtos.map { dto ->
                    MaintenanceRecord(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        title = dto.title,
                        category = dto.category,
                        date = dto.date,
                        odometerKm = dto.odometerKm,
                        notes = dto.notes ?: "",
                        isCompleted = dto.isCompleted
                    )
                }
                if (list.isNotEmpty()) {
                    saveMaintenanceList(list)
                }
                Result.success(list)
            } else {
                Result.success(getMaintenanceRecords())
            }
        } catch (e: Exception) {
            Result.success(getMaintenanceRecords())
        }
    }

    fun getMaintenanceRecords(): List<MaintenanceRecord> {
        val jsonStr = prefs.getString("maintenance_list", null) ?: return getInitialMaintenance()
        val list = mutableListOf<MaintenanceRecord>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    MaintenanceRecord(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        category = obj.getString("category"),
                        date = obj.getString("date"),
                        odometerKm = obj.getInt("odometerKm"),
                        notes = obj.optString("notes", ""),
                        isCompleted = obj.optBoolean("isCompleted", true)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (list.isEmpty()) getInitialMaintenance() else list
    }

    fun addMaintenanceRecord(record: MaintenanceRecord) {
        val list = getMaintenanceRecords().toMutableList()
        list.add(0, record)
        saveMaintenanceList(list)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService.addMaintenanceRecord(
                    MaintenanceDto(
                        title = record.title,
                        category = record.category,
                        date = record.date,
                        odometerKm = record.odometerKm,
                        notes = record.notes,
                        isCompleted = record.isCompleted
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteMaintenanceRecord(id: String) {
        val list = getMaintenanceRecords().filter { it.id != id }
        saveMaintenanceList(list)
    }

    private fun saveMaintenanceList(list: List<MaintenanceRecord>) {
        val array = JSONArray()
        list.forEach { rec ->
            val obj = JSONObject().apply {
                put("id", rec.id)
                put("title", rec.title)
                put("category", rec.category)
                put("date", rec.date)
                put("odometerKm", rec.odometerKm)
                put("notes", rec.notes)
                put("isCompleted", rec.isCompleted)
            }
            array.put(obj)
        }
        prefs.edit().putString("maintenance_list", array.toString()).apply()
    }

    private fun getInitialMaintenance(): List<MaintenanceRecord> {
        return listOf(
            MaintenanceRecord(title = "Engine Oil & Filter Change", category = "Engine Oil", date = "15 Oct 2026", odometerKm = 14000, notes = "Replaced with 15W50 Synthetic"),
            MaintenanceRecord(title = "Chain Clean & Lube", category = "Chain Lube", date = "22 Oct 2026", odometerKm = 14200, notes = "Adjusted slack to 25mm"),
            MaintenanceRecord(title = "Front Brake Pad Inspection", category = "Brakes", date = "01 Sep 2026", odometerKm = 13500, notes = "4mm wear remaining")
        )
    }

    // Trips Persistence & Sync
    suspend fun fetchSavedTripsRemote(): Result<List<Trip>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTrips()
            if (response.isSuccessful && response.body()?.success == true) {
                val dtos = response.body()?.data ?: emptyList()
                val list = dtos.map { dto ->
                    Trip(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        title = dto.title,
                        startLocation = dto.startLocation,
                        destination = dto.destination,
                        distanceKm = dto.distanceKm,
                        estDurationHours = dto.estDurationHours,
                        fuelCostEst = dto.fuelCostEst,
                        date = dto.date,
                        notes = dto.notes ?: ""
                    )
                }
                if (list.isNotEmpty()) {
                    saveTripList(list)
                }
                Result.success(list)
            } else {
                Result.success(getSavedTrips())
            }
        } catch (e: Exception) {
            Result.success(getSavedTrips())
        }
    }

    fun getSavedTrips(): List<Trip> {
        val jsonStr = prefs.getString("trips_list", null) ?: return getInitialTrips()
        val list = mutableListOf<Trip>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Trip(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        startLocation = obj.getString("startLocation"),
                        destination = obj.getString("destination"),
                        distanceKm = obj.getDouble("distanceKm"),
                        estDurationHours = obj.getDouble("estDurationHours"),
                        fuelCostEst = obj.getDouble("fuelCostEst"),
                        date = obj.getString("date"),
                        notes = obj.optString("notes", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (list.isEmpty()) getInitialTrips() else list
    }

    fun addTrip(trip: Trip) {
        val list = getSavedTrips().toMutableList()
        list.add(0, trip)
        saveTripList(list)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService.addTrip(
                    TripDto(
                        title = trip.title,
                        startLocation = trip.startLocation,
                        destination = trip.destination,
                        distanceKm = trip.distanceKm,
                        estDurationHours = trip.estDurationHours,
                        fuelCostEst = trip.fuelCostEst,
                        date = trip.date,
                        notes = trip.notes
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTrip(id: String) {
        val list = getSavedTrips().filter { it.id != id }
        saveTripList(list)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService.deleteTrip(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveTripList(list: List<Trip>) {
        val array = JSONArray()
        list.forEach { t ->
            val obj = JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("startLocation", t.startLocation)
                put("destination", t.destination)
                put("distanceKm", t.distanceKm)
                put("estDurationHours", t.estDurationHours)
                put("fuelCostEst", t.fuelCostEst)
                put("date", t.date)
                put("notes", t.notes)
            }
            array.put(obj)
        }
        prefs.edit().putString("trips_list", array.toString()).apply()
    }

    private fun getInitialTrips(): List<Trip> {
        return listOf(
            Trip(title = "Pune to Mahabaleshwar Weekend Ride", startLocation = "Pune", destination = "Mahabaleshwar", distanceKm = 120.0, estDurationHours = 3.2, fuelCostEst = 420.0, date = "01 Nov 2026"),
            Trip(title = "Lonavala Monsoon Express", startLocation = "Pune", destination = "Lonavala", distanceKm = 65.0, estDurationHours = 1.5, fuelCostEst = 220.0, date = "15 Oct 2026")
        )
    }

    // AI Mechanic Remote Connection
    suspend fun sendMechanicMessageRemote(message: String, model: String): Result<ChatResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.sendMechanicChatMessage(ChatRequestDto(message = message, motorcycleModel = model))
            val chatData = response.body()?.data
            if (response.isSuccessful && response.body()?.success == true && chatData != null) {
                Result.success(chatData)
            } else {
                Result.failure(Exception(response.body()?.message ?: "AI Service Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
