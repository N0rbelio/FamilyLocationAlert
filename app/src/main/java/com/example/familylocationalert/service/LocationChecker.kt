package com.example.familylocationalert.service

import android.location.Location
import com.example.familylocationalert.data.LocationPoint

class LocationChecker {

    private val locationStates =
        mutableMapOf<String, Boolean>()

    fun check(
        currentLocation: Location,
        locations: List<LocationPoint>
    ): List<LocationStatus> {

        return locations.map { location ->

            val targetLocation =
                Location("configured_location").apply {
                    latitude = location.latitude
                    longitude = location.longitude
                }

            val distance =
                currentLocation.distanceTo(
                    targetLocation
                )

            val isInside =
                distance <= location.radiusMeters

            val wasInside =
                locationStates[location.id] ?: false

            if (isInside && !wasInside) {

                println(
                    "ENTROU em ${location.name} " +
                            "(${distance.toInt()} m)"
                )
            }

            if (!isInside && wasInside) {

                println(
                    "SAIU de ${location.name} " +
                            "(${distance.toInt()} m)"
                )
            }

            locationStates[location.id] =
                isInside

            println(
                "Zona: ${location.name} | " +
                        "Distância: ${distance.toInt()} m | " +
                        "Dentro: $isInside"
            )

            LocationStatus(
                locationId = location.id,
                name = location.name,
                distanceMeters = distance,
                isInside = isInside
            )
        }
    }

    fun clear() {
        locationStates.clear()
    }
}