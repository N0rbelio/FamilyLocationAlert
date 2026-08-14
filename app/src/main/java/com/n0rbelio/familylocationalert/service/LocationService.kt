package com.n0rbelio.familylocationalert.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class LocationService(
    private val context: Context
) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        onLocationChanged: (Location) -> Unit

    ) {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        )

            .setMinUpdateIntervalMillis(3000L)
            .build()

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {

                val location = result.lastLocation ?: return

                onLocationChanged(location)
            }
        }
        locationCallback?.let {
            fusedLocationClient.requestLocationUpdates(
                request,
                it,
                Looper.getMainLooper()
            )
        }
    }
    fun stopLocationUpdates() {

        locationCallback?.let {

            fusedLocationClient.removeLocationUpdates(it)
        }

        locationCallback = null
    }
}
