package com.n0rbelio.familylocationalert.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ContactDao {

    @Insert
    suspend fun insert(contact: Contact)

    @Insert
    suspend fun insertAll(contacts: List<Contact>)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("SELECT * FROM contacts ORDER BY name")
    suspend fun getAll(): List<Contact>

    @Query("DELETE FROM contacts")
    suspend fun deleteAll()
}