package com.example.motobuddy.data.repository

import android.content.Context
import com.example.motobuddy.data.local.AuthManager
import com.example.motobuddy.data.model.FuelEntry
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
        val authManager = AuthManager(context)
        val defaultOwner = authManager.getUserName() ?: authManager.getUserEmail()?.substringBefore("@")?.replace(".", " ")?.replaceFirstChar { it.uppercase() } ?: "Rider"
        val make = prefs.getString("moto_make", "Royal Enfield") ?: "Royal Enfield"
        val model = prefs.getString("moto_model", "Himalayan 411") ?: "Himalayan 411"
        val year = prefs.getInt("moto_year", 2022)
        val odo = prefs.getInt("moto_odo", 14250)
        val owner = prefs.getString("moto_owner", defaultOwner) ?: defaultOwner
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

    // Fuel Entries Persistence & Remote Sync
    suspend fun fetchFuelEntriesRemote(): Result<List<FuelEntry>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFuelEntries()
            if (response.isSuccessful && response.body()?.success == true) {
                val dtos = response.body()?.data ?: emptyList()
                val list = dtos.map { dto ->
                    FuelEntry(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        date = dto.date,
                        odometerKm = dto.odometerKm,
                        liters = dto.liters,
                        pricePerLiter = dto.pricePerLiter,
                        totalCost = if (dto.totalCost > 0) dto.totalCost else (dto.liters * dto.pricePerLiter),
                        notes = dto.notes ?: ""
                    )
                }
                if (list.isNotEmpty()) {
                    saveFuelList(list)
                }
                Result.success(list)
            } else {
                Result.success(getFuelEntries())
            }
        } catch (e: Exception) {
            Result.success(getFuelEntries())
        }
    }

    fun getFuelEntries(): List<FuelEntry> {
        val jsonStr = prefs.getString("fuel_list", null) ?: return getInitialFuelEntries()
        val list = mutableListOf<FuelEntry>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    FuelEntry(
                        id = obj.getString("id"),
                        date = obj.getString("date"),
                        odometerKm = obj.getInt("odometerKm"),
                        liters = obj.getDouble("liters"),
                        pricePerLiter = obj.getDouble("pricePerLiter"),
                        totalCost = obj.getDouble("totalCost"),
                        notes = obj.optString("notes", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (list.isEmpty()) getInitialFuelEntries() else list
    }

    fun addFuelEntry(entry: FuelEntry) {
        val list = getFuelEntries().toMutableList()
        list.add(0, entry)
        saveFuelList(list)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService.addFuelEntry(
                    FuelEntryDto(
                        date = entry.date,
                        odometerKm = entry.odometerKm,
                        liters = entry.liters,
                        pricePerLiter = entry.pricePerLiter,
                        totalCost = entry.totalCost,
                        notes = entry.notes
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteFuelEntry(id: String) {
        val list = getFuelEntries().filter { it.id != id }
        saveFuelList(list)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService.deleteFuelEntry(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveFuelList(list: List<FuelEntry>) {
        val array = JSONArray()
        list.forEach { f ->
            val obj = JSONObject().apply {
                put("id", f.id)
                put("date", f.date)
                put("odometerKm", f.odometerKm)
                put("liters", f.liters)
                put("pricePerLiter", f.pricePerLiter)
                put("totalCost", f.totalCost)
                put("notes", f.notes)
            }
            array.put(obj)
        }
        prefs.edit().putString("fuel_list", array.toString()).apply()
    }

    private fun getInitialFuelEntries(): List<FuelEntry> {
        return listOf(
            FuelEntry(date = "01 Nov 2026", odometerKm = 14250, liters = 10.0, pricePerLiter = 104.5, totalCost = 1045.0, notes = "Full tank refill"),
            FuelEntry(date = "15 Oct 2026", odometerKm = 13900, liters = 11.2, pricePerLiter = 104.0, totalCost = 1164.8, notes = "Highway fuel stop")
        )
    }

    fun calculateAverageMileage(entries: List<FuelEntry>): Double {
        if (entries.size < 2) return 32.5
        val sorted = entries.sortedBy { it.odometerKm }
        val first = sorted.first()
        val last = sorted.last()
        val distance = last.odometerKm - first.odometerKm
        val totalLiters = sorted.drop(1).sumOf { it.liters }
        return if (totalLiters > 0 && distance > 0) (distance / totalLiters) else 32.5
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
