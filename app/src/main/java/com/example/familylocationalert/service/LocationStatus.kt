package com.example.familylocationalert.service

enum class LocationEvent {
    NONE,
    ENTERED,
    EXITED
}

data class LocationStatus(
    val locationId: String,
    val name: String,
    val distanceMeters: Float,
    val isInside: Boolean,
    val event: LocationEvent = LocationEvent.NONE
)