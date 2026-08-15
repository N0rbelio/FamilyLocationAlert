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
            "com.n0rbelio.familylocationalert.action.SIMULATE_LOCATION"

        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
    }

    private lateinit var fusedLocationClient:
            FusedLocationProviderClient

    private var locationCallback:
            LocationCallback? = null


    // ================APEMAS PARA SIMULAÇÂO E TESTES================
    private var simulationActive = false
    private var locationProcessingGeneration = 0
    // ==============================================================


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

        if (simulationActive) {
            Log.d(
                "LocationForegroundService",
                "GPS não iniciado: simulação ativa"
            )
            return
        }

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
            currentUpdateInterval
        )
            .setMinUpdateIntervalMillis(
                minOf(
                    currentUpdateInterval,
                    AppConfig.OUTSIDE_MIN_UPDATE_INTERVAL
                )
            )
            .build()

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(
                result: LocationResult
            ) {

                if (simulationActive) {
                    return
                }

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

    private val locationEnteredAt =
        mutableMapOf<String, Long>()


    //private var currentUpdateInterval =
      //  AppConfig.OUTSIDE_UPDATE_INTERVAL

    private var currentUpdateInterval =
        AppConfig.outsideUpdateInterval()

    private fun checkLocations(
        currentLocation: Location
    ) {
        Log.d("LocationChecker", "checkLocations chamado")

        val generation =
            locationProcessingGeneration

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

            if (generation != locationProcessingGeneration) {
                Log.d(
                    "LocationForegroundService",
                    "Resultado descartado: geração antiga"
                )
                return@launch
            }

            val now = System.currentTimeMillis()

            statuses.forEach { status ->

                when (status.event) {

                    LocationEvent.ENTERED -> {
                        locationEnteredAt[status.locationId] = now
                    }

                    LocationEvent.EXITED -> {
                        locationEnteredAt.remove(status.locationId)
                    }

                    LocationEvent.NONE -> Unit
                }
            }

            LocationMonitorState.updateStatuses(
                statuses
            )

            updateLocationInterval(
                statuses
            )

            eventProcessor.process(
                statuses
            )
        }
    }

    private fun calculateUpdateInterval(
        statuses: List<LocationStatus>
    ): Long {

        val now = System.currentTimeMillis()

        val insideStatuses =
            statuses.filter { it.isInside }

        // Não está dentro de nenhuma zona
        if (insideStatuses.isEmpty()) {
            return AppConfig.outsideUpdateInterval()
        }

        // Procuramos há quanto tempo está dentro
        // da zona em que entrou mais recentemente
        var insideTime = Long.MAX_VALUE

        insideStatuses.forEach { status ->

            val enteredAt =
                locationEnteredAt[status.locationId]

            if (enteredAt != null) {

                val time =
                    now - enteredAt

                Log.d(
                    "LocationForegroundService",
                    "Zona ${status.name}: dentro há ${time / 60_000} minutos"
                )

                if (time < insideTime) {
                    insideTime = time
                }

            } else {

                Log.d(
                    "LocationForegroundService",
                    "Zona ${status.name}: dentro, mas hora de entrada desconhecida"
                )

                locationEnteredAt[status.locationId] = now

                // Assumimos que acabou de entrar
                insideTime =
                    0L
            }

        }

       // return when {

        //    insideTime < AppConfig.JUST_ENTERED_UPDATE_INTERVAL ->
      //          AppConfig.JUST_ENTERED_UPDATE_INTERVAL

        //    insideTime < AppConfig.LONG_INSIDE_THRESHOLD ->
       //         AppConfig.INSIDE_UPDATE_INTERVAL

        //    else ->
        //        AppConfig.LONG_INSIDE_UPDATE_INTERVAL
       // }
        return when {

            insideTime < AppConfig.justEnteredUpdateInterval() ->
                AppConfig.justEnteredUpdateInterval()

            insideTime < AppConfig.longInsideThreshold() ->
                AppConfig.insideUpdateInterval()

            else ->
                AppConfig.longInsideUpdateInterval()
        }
    }

    private fun updateLocationInterval(
        statuses: List<LocationStatus>
    ) {

        val newInterval =
            calculateUpdateInterval(statuses)

        if (newInterval == currentUpdateInterval) {
            return
        }

       // Log.d(
        //    "LocationForegroundService",
       //     "Intervalo: " +
        //            "${currentUpdateInterval / 60_000} min → " +
       //             "${newInterval / 60_000} min"
        //)
        Log.d(
            "LocationForegroundService",
            "Intervalo: " +
                    "${currentUpdateInterval / 1000}s → " +
                    "${newInterval / 1000}s"
        )

        currentUpdateInterval = newInterval

        restartLocationUpdates()
    }

    private fun restartLocationUpdates() {

        locationCallback?.let { callback ->

            fusedLocationClient.removeLocationUpdates(
                callback
            )
        }

        locationCallback = null

        if (simulationActive) {
            Log.d(
                "LocationForegroundService",
                "GPS não reiniciado: simulação ativa"
            )
            return
        }

        startLocationUpdates()
    }

    // Apenas utilizado durante desenvolvimento.
    private fun simulateLocation(
        latitude: Double,
        longitude: Double
    ) {

        simulationActive = true
        locationProcessingGeneration++

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

            simulationActive = false
            locationProcessingGeneration++

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