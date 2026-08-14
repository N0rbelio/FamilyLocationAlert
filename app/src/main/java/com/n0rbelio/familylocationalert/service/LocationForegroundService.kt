package com.n0rbelio.familylocationalert.service

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
import com.n0rbelio.familylocationalert.data.DatabaseProvider
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
import android.os.Handler
import android.util.Log
import com.n0rbelio.familylocationalert.config.AppConfig


class LocationForegroundService : Service() {

    private val handler =
        Handler(Looper.getMainLooper())

    private lateinit var eventProcessor: LocationEventProcessor

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


        // apenas para DEV
        //private const val TEST_MODE = true
        private val updateInterval =
            if (AppConfig.TEST_MODE)
                AppConfig.LOCATION_UPDATE_INTERVAL
            else
                5_000L

        private val minUpdateInterval =
            if (AppConfig.TEST_MODE)
                AppConfig.LOCATION_MIN_UPDATE_INTERVAL
            else
                3_000L
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

    private lateinit var locationNotificationManager:
            LocationNotificationManager

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        locationNotificationManager =
            LocationNotificationManager(this)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        LocationMonitorState.setMonitoring(true)

        val smsSender =
            SmsSender(this)

        eventProcessor =
            LocationEventProcessor(
                locationNotificationManager,
                smsSender
            )
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
            updateInterval
        )
            .setMinUpdateIntervalMillis(minUpdateInterval)
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
        Log.d("LocationChecker", "checkLocations chamado")

        serviceScope.launch {

            Log.d("LocationChecker", "Coroutine iniciada")

            val locations =
                locationDao.getAll()

            Log.d("LocationChecker", "Locais encontrados=${locations.size}")

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

            eventProcessor.process(
                statuses
            )
        }
    }


    // Apenas utilizado durante desenvolvimento.
    private fun simulateLocation(
        latitude: Double,
        longitude: Double
    ) {

        println(
            "LocationForegroundService: GPS pausado para teste"
        )

        stopLocationUpdates()

        val simulatedLocation =
            Location("simulation").apply {
                this.latitude = latitude
                this.longitude = longitude
            }

        println(
            "LocationForegroundService: SIMULAÇÃO $latitude, $longitude"
        )

        LocationMonitorState.updateLocation(
            simulatedLocation
        )

        checkLocations(
            simulatedLocation
        )

        handler.postDelayed({

            println(
                "LocationForegroundService: GPS retomado"
            )

            startLocationUpdates()

        }, 20_000)
    }

    private fun stopLocationUpdates() {

        locationCallback?.let { callback ->

            fusedLocationClient.removeLocationUpdates(
                callback
            )
        }

        locationCallback = null

        LocationMonitorState.clear()

        println(
            "LocationForegroundService: " +
                    "localização parada"
        )
    }

    override fun onDestroy() {

        LocationMonitorState.setMonitoring(false)

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