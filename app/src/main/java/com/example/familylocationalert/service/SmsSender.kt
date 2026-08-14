package com.example.familylocationalert.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat

class SmsSender(
    private val context: Context
) {

    fun send(
        phoneNumber: String,
        message: String
    ) {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            println(
                "SmsSender: permissão SEND_SMS não concedida"
            )

            return
        }

        try {

            val smsManager =
                context.getSystemService(
                    SmsManager::class.java
                )

            smsManager.sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
            )

            println(
                "SmsSender: SMS enviado com sucesso"
            )

        } catch (e: Exception) {

            println(
                "SmsSender: erro ao enviar SMS"
            )

            println(
                "SmsSender: ${e.message}"
            )
        }
    }
}