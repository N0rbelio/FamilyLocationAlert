package com.example.familylocationalert.service

import android.location.Location
import com.example.familylocationalert.data.LocationPoint

class LocationChecker {

    private val locationStates =
        mutableMapOf<String, Boolean?>()

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

            val previousInside =
                locationStates[location.id]

            val event =
                when (previousInside) {

                    null -> {
                        // Primeira localização conhecida.
                        // Apenas inicializamos o estado.
                        LocationEvent.NONE
                    }

                    false -> {
                        if (isInside) {
                            LocationEvent.ENTERED
                        } else {
                            LocationEvent.NONE
                        }
                    }

                    true -> {
                        if (!isInside) {
                            LocationEvent.EXITED
                        } else {
                            LocationEvent.NONE
                        }
                    }
                }

            locationStates[location.id] =
                isInside

            //println(
            //    "Zona=${location.name} " +
            //            "Dist=${distance.toInt()}m " +
           //             "Dentro=$isInside " +
           //             "Anterior=$previousState " +
          //              "Evento=$event"
          //  )
            android.util.Log.d(
                        "LocationChecker",
                "Zona=${location.name} Dist=${distance.toInt()}m Dentro=$isInside Anterior=$previousInside Evento=$event"
            )

            LocationStatus(
                locationId = location.id,
                name = location.name,
                distanceMeters = distance,
                isInside = isInside,
                event = event
            )
        }
    }

    fun clear() {
        locationStates.clear()
    }
}