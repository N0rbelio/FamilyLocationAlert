package com.n0rbelio.familylocationalert.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "locations")
data class LocationPoint(
    @PrimaryKey
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    val smsAlertMode: SmsAlertMode = SmsAlertMode.BOTH,
    val trackTime: Boolean = false
)