package com.n0rbelio.familylocationalert.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.n0rbelio.familylocationalert.data.DatabaseProvider
import com.n0rbelio.familylocationalert.data.LocationPoint
import kotlinx.coroutines.launch
import android.content.Intent
import androidx.core.content.ContextCompat
import com.n0rbelio.familylocationalert.service.LocationForegroundService
import com.n0rbelio.familylocationalert.service.LocationMonitorState
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

            LocationMonitorState.monitoring.collectLatest {

                monitoring = it
            }
        }

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

    }

    fun stopMonitoring() {

        val intent = Intent(
            getApplication(),
            LocationForegroundService::class.java
        )

        getApplication<Application>()
            .stopService(intent)

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