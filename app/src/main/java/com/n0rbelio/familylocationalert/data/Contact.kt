package com.n0rbelio.familylocationalert.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey
    val id: String,
    val name: String,
    val phoneNumber: String
)