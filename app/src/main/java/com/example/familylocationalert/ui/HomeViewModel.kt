package com.example.familylocationalert.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.familylocationalert.data.DatabaseProvider
import com.example.familylocationalert.data.LocationPoint
import kotlinx.coroutines.launch
import android.location.Location
import com.example.familylocationalert.data.DefaultLocations
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.familylocationalert.service.LocationForegroundService
import androidx.lifecycle.viewModelScope
import com.example.familylocationalert.service.LocationMonitorState
import kotlinx.coroutines.flow.collectLatest

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


    private val locationDao =
        DatabaseProvider.getDatabase(getApplication()).locationDao()

    init {
        loadLocations()
        observeLocationService()
    }




    private fun observeLocationService() {

        viewModelScope.launch {

            LocationMonitorState.currentLocation.collectLatest { location ->

                latitude = location?.latitude
                longitude = location?.longitude
            }
        }

        viewModelScope.launch {

            LocationMonitorState.locationStatuses.collectLatest { statuses ->

                if (statuses.isEmpty()) {
                    testResult = ""
                    return@collectLatest
                }

                val result = StringBuilder()

                result.appendLine("📍 Posição atual:")

                val currentLat = latitude
                val currentLon = longitude

                if (currentLat != null && currentLon != null) {

                    result.appendLine(
                        "$currentLat, $currentLon"
                    )
                }

                result.appendLine()

                statuses.forEach { status ->

                    result.appendLine(
                        "📌 ${status.name}"
                    )

                    result.appendLine(
                        "Distância: " +
                                "${status.distanceMeters.toInt()} m"
                    )

                    if (status.isInside) {

                        result.appendLine(
                            "✅ Dentro da zona"
                        )

                    } else {

                        result.appendLine(
                            "❌ Fora da zona"
                        )
                    }

                    result.appendLine()
                }

                testResult = result.toString()
            }
        }
    }



    private fun loadLocations() {
        viewModelScope.launch {
            locations = locationDao.getAll()

            println(
                "HomeViewModel: " +
                        "locais carregados = ${locations.size}"
            )

            locations.forEach {
                println(
                    "HomeViewModel: " +
                            "${it.name} -> ${it.latitude}, ${it.longitude}"
                )
            }
        }
    }

    fun startMonitoring() {

        val intent = Intent(
            getApplication(),
            LocationForegroundService::class.java
        )

        ContextCompat.startForegroundService(
            getApplication(),
            intent
        )

        monitoring = true
    }

    private val locationStates =
        mutableMapOf<String, Boolean>()

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

        val intent = Intent(
            getApplication(),
            LocationForegroundService::class.java
        )

        getApplication<Application>()
            .stopService(intent)

        monitoring = false

        latitude = null
        longitude = null

        locationStates.clear()

        LocationMonitorState.clear()
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

            println(
                "HomeViewModel: " +
                        "locais após guardar = ${locations.size}"
            )

            locations.forEach {
                println(
                    "HomeViewModel: " +
                            "${it.name} -> ${it.latitude}, ${it.longitude}"
                )
            }
        }
    }

    fun deleteLocation(location: LocationPoint) {
        viewModelScope.launch {
            locationDao.delete(location)
            locations = locationDao.getAll()
        }
    }

    fun simulateLocation(
        latitude: Double,
        longitude: Double
    ) {

        val intent = Intent(
            getApplication(),
            LocationForegroundService::class.java
        ).apply {

            action =
                LocationForegroundService.ACTION_SIMULATE_LOCATION

            putExtra(
                LocationForegroundService.EXTRA_LATITUDE,
                latitude
            )

            putExtra(
                LocationForegroundService.EXTRA_LONGITUDE,
                longitude
            )
        }

        ContextCompat.startForegroundService(
            getApplication(),
            intent
        )
    }
}