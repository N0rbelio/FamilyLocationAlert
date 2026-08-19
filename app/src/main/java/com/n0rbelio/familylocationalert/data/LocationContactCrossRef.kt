package com.n0rbelio.familylocationalert.data

import androidx.room.Entity

@Entity(
    tableName = "location_contacts",
    primaryKeys = ["locationId", "contactId"]
)
data class LocationContactCrossRef(
    val locationId: String,
    val contactId: String
)