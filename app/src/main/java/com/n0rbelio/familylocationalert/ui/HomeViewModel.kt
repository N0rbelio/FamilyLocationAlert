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
import com.n0rbelio.familylocationalert.data.Contact
import com.n0rbelio.familylocationalert.data.LocationContactCrossRef
import android.Manifest
import android.location.Location
import android.content.pm.PackageManager
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.annotation.SuppressLint
import com.n0rbelio.familylocationalert.data.SmsAlertMode


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

    var selectedContactIds by mutableStateOf<Set<String>>(emptySet())
        private set


    private val locationDao =
        DatabaseProvider.getDatabase(getApplication()).locationDao()

    private val contactDao =
        DatabaseProvider.getDatabase(getApplication()).contactDao()

    private val locationContactDao =
        DatabaseProvider.getDatabase(getApplication()).locationContactDao()


    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onResult: (Double, Double) -> Unit,
        onError: () -> Unit
    ) {

        val context = getApplication<Application>()

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        val request =
            CurrentLocationRequest.Builder()
                .setPriority(
                    Priority.PRIORITY_HIGH_ACCURACY
                )
                .setMaxUpdateAgeMillis(5_000)
                .build()

        fusedLocationClient
            .getCurrentLocation(request, null)
            .addOnSuccessListener { location ->

                if (location != null) {

                    onResult(
                        location.latitude,
                        location.longitude
                    )

                } else {

                    onError()
                }
            }
            .addOnFailureListener {

                onError()
            }
    }

//    fun getCurrentLocation(
//        onResult: (latitude: Double, longitude: Double) -> Unit,
//        onError: () -> Unit
//    ) {
//        val context = getApplication<Application>()
//
//        if (
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED &&
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            onError()
//            return
//        }
//
//        val fusedLocationClient =
//            LocationServices.getFusedLocationProviderClient(context)
//
//        val request = CurrentLocationRequest.Builder()
//            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//            .setMaxUpdateAgeMillis(5_000)
//            .build()
//
//        fusedLocationClient
//            .getCurrentLocation(request, null)
//            .addOnSuccessListener { location ->
//
//                if (location != null) {
//
//                    onResult(
//                        location.latitude,
//                        location.longitude
//                    )
//
//                } else {
//
//                    onError()
//                }
//            }
//            .addOnFailureListener {
//
//                onError()
//            }
//    }

    init {
        loadLocations()
        observeLocationService()
    }


    fun updateLocation(
        id: String,
        name: String,
        latitude: Double,
        longitude: Double,
        radiusMeters: Float,
        smsAlertMode: SmsAlertMode,
        trackTime: Boolean
    ) {
        viewModelScope.launch {

            locationDao.update(
                id = id,
                name = name,
                latitude = latitude,
                longitude = longitude,
                radiusMeters = radiusMeters,
                smsAlertMode = smsAlertMode,
                trackTime = trackTime
            )

            locations = locationDao.getAll()
        }
    }


    suspend fun getContactIdsForLocation(
        locationId: String
    ): Set<String> {

        return locationContactDao
            .getContactIdsForLocation(locationId)
            .toSet()
    }

    fun loadContactsForLocation(
        locationId: String
    ) {
        viewModelScope.launch {

            selectedContactIds =
                locationContactDao
                    .getContactIdsForLocation(locationId)
                    .toSet()
        }
    }


    fun saveLocationContacts(
        locationId: String,
        contactIds: Set<String>
    ) {

        viewModelScope.launch {

            locationContactDao
                .deleteContactsFromLocation(locationId)

            contactIds.forEach { contactId ->

                locationContactDao.insert(
                    LocationContactCrossRef(
                        locationId = locationId,
                        contactId = contactId
                    )
                )
            }
        }
    }

    suspend fun getContactsForLocation(
        locationId: String
    ): List<Contact> {

        val contactIds =
            locationContactDao.getContactIdsForLocation(
                locationId
            )

        if (contactIds.isEmpty()) {
            return emptyList()
        }

        return contactDao
            .getAll()
            .filter {
                it.id in contactIds
            }
    }


    fun saveContactsForLocation(
        locationId: String,
        contactIds: List<String>
    ) {

        viewModelScope.launch {

            locationContactDao
                .deleteContactsFromLocation(
                    locationId
                )

            contactIds.forEach { contactId ->

                locationContactDao.insert(
                    LocationContactCrossRef(
                        locationId = locationId,
                        contactId = contactId
                    )
                )
            }
        }
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
    }

    fun saveLocation(
        name: String,
        latitude: Double,
        longitude: Double,
        radiusMeters: Float,
        contactIds: Set<String>,
        smsAlertMode: SmsAlertMode,
        trackTime: Boolean
    ) {
        viewModelScope.launch {

            val location = LocationPoint(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                latitude = latitude,
                longitude = longitude,
                radiusMeters = radiusMeters,
                smsAlertMode = smsAlertMode,
                trackTime = trackTime
            )

            locationDao.insert(location)

            contactIds.forEach { contactId ->

                locationContactDao.insert(
                    LocationContactCrossRef(
                        locationId = location.id,
                        contactId = contactId
                    )
                )
            }

            locations = locationDao.getAll()

            println(
                "HomeViewModel: " +
                        "zona criada = ${location.name}"
            )

            println(
                "HomeViewModel: " +
                        "contactos associados = ${contactIds.size}"
            )
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

    fun resumeRealLocation() {

        val intent = Intent(
            getApplication(),
            LocationForegroundService::class.java
        ).apply {

            action =
                LocationForegroundService
                    .ACTION_RESUME_REAL_LOCATION
        }

        ContextCompat.startForegroundService(
            getApplication(),
            intent
        )
    }

}