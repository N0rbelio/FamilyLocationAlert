package com.example.familylocationalert.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.familylocationalert.MainActivity

class LocationNotificationManager(
    private val context: Context
) {

    companion object {
        const val CHANNEL_ID = "location_events"
    }

    private val notificationManager =
        context.getSystemService(
            NotificationManager::class.java
        )

    init {
        createChannel()
    }

    private fun createChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alertas de localização",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description =
                "Notificações quando entra ou sai de uma zona"
        }

        notificationManager.createNotificationChannel(
            channel
        )
    }

    fun notifyEntered(
        locationName: String
    ) {

        showNotification(
            title = "📍 Entraste em $locationName",
            text = "Estás dentro da zona configurada."
        )
    }

    fun notifyExited(
        locationName: String
    ) {

        showNotification(
            title = "📍 Saíste de $locationName",
            text = "Deixaste a zona configurada."
        )
    }

    private fun showNotification(
        title: String,
        text: String
    ) {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println(
                "LocationNotificationManager: " +
                        "permissão de notificações não concedida"
            )

            return
        }

        val intent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    android.R.drawable.ic_menu_mylocation
                )
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}