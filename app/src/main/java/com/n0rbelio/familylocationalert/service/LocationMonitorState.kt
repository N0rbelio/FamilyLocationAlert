package com.n0rbelio.familylocationalert.service

import android.location.Location
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocationMonitorState {

    private val _monitoring =
        MutableStateFlow(false)

    val monitoring: StateFlow<Boolean> =
        _monitoring.asStateFlow()


    private val _currentLocation =
        MutableStateFlow<Location?>(null)

    val currentLocation: StateFlow<Location?> =
        _currentLocation.asStateFlow()


    private val _locationStatuses =
        MutableStateFlow<List<LocationStatus>>(emptyList())

    val locationStatuses: StateFlow<List<LocationStatus>> =
        _locationStatuses.asStateFlow()


    // ─────────────────────────────────────────
    // MODO DE PRECISÃO
    // ─────────────────────────────────────────

    private val _locationPriority =
        MutableStateFlow(
            Priority.PRIORITY_HIGH_ACCURACY
        )

    val locationPriority: StateFlow<Int> =
        _locationPriority.asStateFlow()


    fun setMonitoring(value: Boolean) {
        _monitoring.value = value
    }

    fun updateLocation(location: Location) {
        _currentLocation.value = location
    }

    fun updateStatuses(
        statuses: List<LocationStatus>
    ) {
        _locationStatuses.value = statuses
    }

    fun updatePriority(priority: Int) {
        _locationPriority.value = priority
    }

    fun clear() {
        _currentLocation.value = null
        _locationStatuses.value = emptyList()
    }
}