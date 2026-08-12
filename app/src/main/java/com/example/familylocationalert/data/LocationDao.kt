package com.example.familylocationalert.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {

    @Insert
    suspend fun insert(location: LocationPoint)

    @Delete
    suspend fun delete(location: LocationPoint)

    @Query("SELECT * FROM locations")
    suspend fun getAll(): List<LocationPoint>

    @Query("DELETE FROM locations")
    suspend fun deleteAll()
}