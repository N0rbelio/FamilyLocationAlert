package com.example.familylocationalert.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.familylocationalert.data.DatabaseProvider
import com.example.familylocationalert.data.LocationPoint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class LocationForegroundService : Service() {

    private val serviceScope =
        CoroutineScope(
            SupervisorJob() + Dispatchers.IO
        )

    companion object {

        const val CHANNEL_ID = "location_monitoring"
        const val NOTIFICATION_ID = 1001

        const val ACTION_SIMULATE_LOCATION =
            "com.example.familylocationalert.action.SIMULATE_LOCATION"

        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
    }

    private lateinit var fusedLocationClient:
            FusedLocationProviderClient

    private var locationCallback:
            LocationCallback? = null

    private val locationDao by lazy {
        DatabaseProvider
            .getDatabase(applicationContext)
            .locationDao()
    }

    private val locationChecker =
        LocationChecker()



    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        startForeground(
            NOTIFICATION_ID,
            createNotification()
        )

        startLocationUpdates()

        if (
            intent?.action ==
            ACTION_SIMULATE_LOCATION
        ) {

            val latitude =
                intent.getDoubleExtra(
                    EXTRA_LATITUDE,
                    Double.NaN
                )

            val longitude =
                intent.getDoubleExtra(
                    EXTRA_LONGITUDE,
                    Double.NaN
                )

            if (
                !latitude.isNaN() &&
                !longitude.isNaN()
            ) {

                simulateLocation(
                    latitude,
                    longitude
                )
            }
        }

        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            println(
                "LocationForegroundService: " +
                        "permissão de localização não concedida"
            )

            stopSelf()
            return
        }

        if (locationCallback != null) {
            return
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        )
            .setMinUpdateIntervalMillis(3000L)
            .build()

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(
                result: LocationResult
            ) {

                val location =
                    result.lastLocation
                        ?: return

                println(
                    "LocationForegroundService: " +
                            "${location.latitude}, ${location.longitude}"
                )

                LocationMonitorState.updateLocation(location)

                checkLocations(location)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback!!,
            Looper.getMainLooper()
        )

        println(
            "LocationForegroundService: " +
                    "localização iniciada"
        )
    }

    private fun checkLocations(
        currentLocation: Location
    ) {

        serviceScope.launch {

            val locations =
                locationDao.getAll()

            if (locations.isEmpty()) {

                LocationMonitorState.updateStatuses(
                    emptyList()
                )

                return@launch
            }

            val statuses =
                locationChecker.check(
                    currentLocation,
                    locations
                )

            LocationMonitorState.updateStatuses(
                statuses
            )
        }
    }

    private fun simulateLocation(
        latitude: Double,
        longitude: Double
    ) {

        val simulatedLocation =
            Location("simulation").apply {

                this.latitude = latitude
                this.longitude = longitude
            }

        println(
            "LocationForegroundService: " +
                    "SIMULAÇÃO $latitude, $longitude"
        )

        LocationMonitorState.updateLocation(
            simulatedLocation
        )

        checkLocations(
            simulatedLocation
        )
    }

    private fun stopLocationUpdates() {

        locationCallback?.let { callback ->

            fusedLocationClient.removeLocationUpdates(
                callback
            )
        }

        locationCallback = null

        locationChecker.clear()

        LocationMonitorState.clear()

        println(
            "LocationForegroundService: " +
                    "localização parada"
        )
    }

    override fun onDestroy() {

        stopLocationUpdates()

        serviceScope.cancel()

        super.onDestroy()

        println(
            "LocationForegroundService: " +
                    "Service destruído"
        )
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {
        return null
    }

    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Monitorização de localização",
            NotificationManager.IMPORTANCE_LOW
        ).apply {

            description =
                "Mantém a monitorização de localização ativa"
        }

        val notificationManager =
            getSystemService(
                NotificationManager::class.java
            )

        notificationManager.createNotificationChannel(
            channel
        )
    }

    private fun createNotification(): Notification {

        return Notification.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle(
                "Family Location Alert"
            )
            .setContentText(
                "Monitorização de localização ativa"
            )
            .setSmallIcon(
                android.R.drawable.ic_menu_mylocation
            )
            .setOngoing(true)
            .build()
    }
}