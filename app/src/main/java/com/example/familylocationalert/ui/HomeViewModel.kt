package com.example.familylocationalert.ui
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.familylocationalert.service.LocationService

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    var monitoring by mutableStateOf(false)
        private set

    var latitude by mutableStateOf<Double?>(null)
        private set

    var longitude by mutableStateOf<Double?>(null)
        private set

    private val locationService = LocationService(getApplication())

    fun startMonitoring() {

        monitoring = true

        locationService.startLocationUpdates { location ->

            latitude = location.latitude
            longitude = location.longitude
        }
    }

    fun stopMonitoring() {

        monitoring = false

        locationService.stopLocationUpdates()
    }


}