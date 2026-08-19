package com.n0rbelio.familylocationalert.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        LocationPoint::class,
        Contact::class,
        LocationContactCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
    abstract fun contactDao(): ContactDao
    abstract fun locationContactDao(): LocationContactDao
}