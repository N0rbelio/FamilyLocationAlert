package com.example.familylocationalert.data

data class LocationPoint(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    val phoneNumbers: List<String>
)