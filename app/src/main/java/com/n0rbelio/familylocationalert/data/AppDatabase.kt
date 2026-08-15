package com.n0rbelio.familylocationalert.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LocationPoint::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
}