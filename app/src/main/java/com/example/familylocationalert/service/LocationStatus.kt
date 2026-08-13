package com.example.familylocationalert.service

data class LocationStatus(
    val locationId: String,
    val name: String,
    val distanceMeters: Float,
    val isInside: Boolean
)