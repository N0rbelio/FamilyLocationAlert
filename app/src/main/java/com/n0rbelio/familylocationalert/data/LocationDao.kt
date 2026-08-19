package com.n0rbelio.familylocationalert.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDao {

    @Insert
    suspend fun insert(location: LocationPoint)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaults(locations: List<LocationPoint>)

    @Delete
    suspend fun delete(location: LocationPoint)

    @Query("SELECT * FROM locations")
    suspend fun getAll(): List<LocationPoint>

    @Query("SELECT * FROM locations WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): LocationPoint?

    @Query("DELETE FROM locations")
    suspend fun deleteAll()

    @Query("""
        UPDATE locations
        SET name = :name,
            latitude = :latitude,
            longitude = :longitude,
            radiusMeters = :radiusMeters,
            smsAlertMode = :smsAlertMode,
            trackTime = :trackTime
        WHERE id = :id
    """)

    suspend fun update(
        id: String,
        name: String,
        latitude: Double,
        longitude: Double,
        radiusMeters: Float,
        smsAlertMode: SmsAlertMode,
        trackTime: Boolean
    )
}