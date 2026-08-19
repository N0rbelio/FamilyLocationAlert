package com.n0rbelio.familylocationalert.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        relation: LocationContactCrossRef
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(
        relations: List<LocationContactCrossRef>
    )

    @Query(
        "DELETE FROM location_contacts " +
                "WHERE locationId = :locationId"
    )
    suspend fun deleteContactsFromLocation(
        locationId: String
    )

    @Query(
        "SELECT contactId FROM location_contacts " +
                "WHERE locationId = :locationId"
    )
    suspend fun getContactIdsForLocation(
        locationId: String
    ): List<String>

    @Query(
        "SELECT locationId FROM location_contacts " +
                "WHERE contactId = :contactId"
    )
    suspend fun getLocationIdsForContact(
        contactId: String
    ): List<String>
}
