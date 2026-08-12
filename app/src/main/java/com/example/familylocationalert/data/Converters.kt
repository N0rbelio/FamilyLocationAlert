package com.example.familylocationalert.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromPhoneNumbers(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toPhoneNumbers(value: String): List<String> {
        return if (value.isBlank()) {
            emptyList()
        } else {
            value.split(",")
        }
    }
}