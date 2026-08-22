package com.n0rbelio.familylocationalert.config

import com.google.android.gms.location.Priority

object AppConfig {

    // Desenvolvimento
    const val TEST_MODE = true

    // Localização
    const val OUTSIDE_UPDATE_INTERVAL = 60_000L              // 1 minuto
    const val JUST_ENTERED_UPDATE_INTERVAL = 1 * 60_000L   // 15 minutos
    const val INSIDE_UPDATE_INTERVAL = 60 * 60_000L         // 1 hora
    const val LONG_INSIDE_UPDATE_INTERVAL = 3 * 60 * 60_000L // 3 horas

    const val LONG_INSIDE_THRESHOLD = 60 * 60_000L          // 1 hora


    // Prioridade de localização
    val OUTSIDE_LOCATION_PRIORITY = Priority.PRIORITY_HIGH_ACCURACY

    val INSIDE_LOCATION_PRIORITY =
        Priority.PRIORITY_BALANCED_POWER_ACCURACY


    // Localização — valores para testes
    const val TEST_OUTSIDE_UPDATE_INTERVAL = 10_000L        // 10 s
    const val TEST_JUST_ENTERED_UPDATE_INTERVAL = 20_000L   // 20 s
    const val TEST_INSIDE_UPDATE_INTERVAL = 30_000L         // 30 s
    const val TEST_LONG_INSIDE_UPDATE_INTERVAL = 60_000L    // 1 min

    const val TEST_LONG_INSIDE_THRESHOLD = 60_000L          // 1 min


    fun outsideUpdateInterval(): Long =
        if (TEST_MODE)
            TEST_OUTSIDE_UPDATE_INTERVAL
        else
            OUTSIDE_UPDATE_INTERVAL

    fun justEnteredUpdateInterval(): Long =
        if (TEST_MODE)
            TEST_JUST_ENTERED_UPDATE_INTERVAL
        else
            JUST_ENTERED_UPDATE_INTERVAL

    fun insideUpdateInterval(): Long =
        if (TEST_MODE)
            TEST_INSIDE_UPDATE_INTERVAL
        else
            INSIDE_UPDATE_INTERVAL

    fun longInsideUpdateInterval(): Long =
        if (TEST_MODE)
            TEST_LONG_INSIDE_UPDATE_INTERVAL
        else
            LONG_INSIDE_UPDATE_INTERVAL

    fun longInsideThreshold(): Long =
        if (TEST_MODE)
            TEST_LONG_INSIDE_THRESHOLD
        else
            LONG_INSIDE_THRESHOLD
}