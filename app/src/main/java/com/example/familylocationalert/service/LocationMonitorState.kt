package com.example.familylocationalert.service

import android.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocationMonitorState {

    private val _currentLocation =
        MutableStateFlow<Location?>(null)

    val currentLocation: StateFlow<Location?> =
        _currentLocation.asStateFlow()

    private val _monitoring =
        MutableStateFlow(false)

    val monitoring: StateFlow<Boolean> =
        _monitoring.asStateFlow()

    fun setMonitoring(
        monitoring: Boolean
    ) {
        _monitoring.value = monitoring
    }

    private val _locationStatuses =
        MutableStateFlow<List<LocationStatus>>(emptyList())

    val locationStatuses: StateFlow<List<LocationStatus>> =
        _locationStatuses.asStateFlow()

    fun updateLocation(location: Location) {
        _currentLocation.value = location
    }

    fun updateStatuses(
        statuses: List<LocationStatus>
    ) {
        _locationStatuses.value = statuses
    }

    fun clear() {
        _currentLocation.value = null
        _locationStatuses.value = emptyList()
    }

    //fun simulateLocation(
     //   location: Location
    //) {
   //     _currentLocation.value = location
    //}

}