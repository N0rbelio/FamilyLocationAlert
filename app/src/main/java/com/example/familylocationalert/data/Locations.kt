package com.example.familylocationalert.data

object Locations {

    private val locations = mutableListOf<LocationPoint>()

    fun add(location: LocationPoint) {
        locations.add(location)
    }

    fun getAll(): List<LocationPoint> {
        return locations.toList()
    }

    fun clear() {
        locations.clear()
    }
}