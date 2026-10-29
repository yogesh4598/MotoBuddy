package com.example.motobuddy.data.repository

import android.content.Context
import com.example.motobuddy.data.model.MaintenanceRecord
import com.example.motobuddy.data.model.Motorcycle
import com.example.motobuddy.data.model.Trip
import org.json.JSONArray
import org.json.JSONObject

class MotoRepository(context: Context) {
    private val prefs = context.getSharedPreferences("motobuddy_local_store", Context.MODE_PRIVATE)

    // Motorcycle details
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
    }

    // Maintenance Records
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

    // Trips Persistence
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
    }

    fun deleteTrip(id: String) {
        val list = getSavedTrips().filter { it.id != id }
        saveTripList(list)
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
}
