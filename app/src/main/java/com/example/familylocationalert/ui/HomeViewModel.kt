package com.example.familylocationalert.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.familylocationalert.data.DatabaseProvider
import com.example.familylocationalert.data.LocationPoint

import com.example.familylocationalert.service.LocationService
import kotlinx.coroutines.launch
import android.location.Location


class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    var monitoring by mutableStateOf(false)
        private set

    var latitude by mutableStateOf<Double?>(null)
        private set

    var longitude by mutableStateOf<Double?>(null)
        private set

    var locations by mutableStateOf<List<LocationPoint>>(emptyList())
        private set

    var testResult by mutableStateOf("")
        private set

    private val locationService = LocationService(getApplication())

    private val locationDao =
        DatabaseProvider.getDatabase(getApplication()).locationDao()

    init {
        loadLocations()
    }

    private val locationStates = mutableMapOf<String, Boolean>()

    private fun loadLocations() {
        viewModelScope.launch {
            locations = locationDao.getAll()
        }
    }

    fun startMonitoring() {

        monitoring = true

        locationService.startLocationUpdates { currentLocation ->

            latitude = currentLocation.latitude
            longitude = currentLocation.longitude

            checkLocations(currentLocation)
        }
    }

    private fun checkLocations(currentLocation: Location) {

        if (locations.isEmpty()) {
            testResult = "Não existem locais configurados."
            return
        }

        val result = StringBuilder()

        result.appendLine(
            "📍 Posição atual:"
        )

        result.appendLine(
            "${currentLocation.latitude}, ${currentLocation.longitude}"
        )

        result.appendLine()

        locations.forEach { location ->

            val targetLocation = Location("configured_location").apply {
                latitude = location.latitude
                longitude = location.longitude
            }

            val distance = currentLocation.distanceTo(targetLocation)

            val isInside = distance <= location.radiusMeters
            val wasInside = locationStates[location.id] ?: false

            if (isInside && !wasInside) {
                println("ENTROU em ${location.name}")
            }

            if (!isInside && wasInside) {
                println("SAIU de ${location.name}")
            }

            locationStates[location.id] = isInside

            result.appendLine("📌 ${location.name}")
            result.appendLine("Distância: ${distance.toInt()} m")

            if (isInside) {
                result.appendLine("✅ Dentro da zona")
            } else {
                result.appendLine("❌ Fora da zona")
            }

            result.appendLine()
        }

        testResult = result.toString()
    }

    fun stopMonitoring() {

        monitoring = false

        latitude = null
        longitude = null

        locationService.stopLocationUpdates()

        locationStates.clear()

        locationService.stopLocationUpdates()
    }

    fun saveLocation(
        name: String,
        latitude: Double,
        longitude: Double,
        radiusMeters: Float
    ) {
        viewModelScope.launch {

            val location = LocationPoint(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                latitude = latitude,
                longitude = longitude,
                radiusMeters = radiusMeters,
                phoneNumbers = emptyList()
            )

            locationDao.insert(location)

            locations = locationDao.getAll()
        }
    }

    fun deleteLocation(location: LocationPoint) {
        viewModelScope.launch {
            locationDao.delete(location)
            locations = locationDao.getAll()
        }
    }

    fun simulateLocation(latitude: Double, longitude: Double) {

        val simulatedLocation = Location("simulation").apply {
            this.latitude = latitude
            this.longitude = longitude
        }

        this.latitude = latitude
        this.longitude = longitude

        checkLocations(simulatedLocation)
    }
}