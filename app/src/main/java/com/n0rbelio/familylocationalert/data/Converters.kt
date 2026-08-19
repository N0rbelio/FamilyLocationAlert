package com.n0rbelio.familylocationalert.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromSmsAlertMode(value: SmsAlertMode): String {
        return value.name
    }

    @TypeConverter
    fun toSmsAlertMode(value: String): SmsAlertMode {
        return SmsAlertMode.valueOf(value)
    }
}