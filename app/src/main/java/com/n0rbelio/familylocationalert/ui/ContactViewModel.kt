package com.n0rbelio.familylocationalert.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.n0rbelio.familylocationalert.data.Contact
import com.n0rbelio.familylocationalert.data.DatabaseProvider
import kotlinx.coroutines.launch
import java.util.UUID

class ContactViewModel(
    application: Application
) : AndroidViewModel(application) {

    var contacts by mutableStateOf<List<Contact>>(emptyList())
        private set

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            contacts =
                DatabaseProvider
                    .getDatabase(getApplication())
                    .contactDao()
                    .getAll()
        }
    }

    fun addContact(
        name: String,
        phoneNumber: String
    ) {
        if (
            name.isBlank() ||
            phoneNumber.isBlank()
        ) {
            return
        }

        viewModelScope.launch {

            val contact = Contact(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                phoneNumber = phoneNumber.trim()
            )

            DatabaseProvider
                .getDatabase(getApplication())
                .contactDao()
                .insert(contact)

            loadContacts()
        }
    }

    fun deleteContact(
        contact: Contact
    ) {
        viewModelScope.launch {

            DatabaseProvider
                .getDatabase(getApplication())
                .contactDao()
                .delete(contact)

            loadContacts()
        }
    }
}