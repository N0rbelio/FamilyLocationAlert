package com.n0rbelio.familylocationalert.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.n0rbelio.familylocationalert.data.Contact
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun ContactsScreen(
    onBackClick: () -> Unit
) {

    val contactViewModel: ContactViewModel = viewModel()

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                horizontal = 15.dp,
                vertical = 2.dp
            )
    ) {

        // HEADER

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Text(
                text = "Contactos",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            Button(
                onClick = {
                    showAddDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Text("Adicionar contacto")
            }

            Spacer(
                modifier = Modifier.size(16.dp)
            )

            if (contactViewModel.contacts.isEmpty()) {

                Text(
                    text = "Ainda não existem contactos.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    items(
                        items = contactViewModel.contacts,
                        key = { it.id }
                    ) { contact ->

                        ContactItem(
                            contact = contact,
                            onDelete = {
                                contactViewModel.deleteContact(
                                    contact
                                )
                            }
                        )
                    }
                }
            }
        }

    }

    if (showAddDialog) {

        AddContactDialog(

            onDismiss = {
                showAddDialog = false
            },

            onSave = { name, phoneNumber ->

                contactViewModel.addContact(
                    name,
                    phoneNumber
                )

                showAddDialog = false
            }
        )
    }
}


@Composable
private fun ContactItem(
    contact: Contact,
    onDelete: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.shapes.medium
            )
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = contact.phoneNumber,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = onDelete
        ) {

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Apagar contacto",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}


@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onSave: (
        String,
        String
    ) -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var phoneNumber by remember {
        mutableStateOf("")
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Adicionar contacto")
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Nome")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { value ->
                        if (value.all { it.isDigit() }) {
                            phoneNumber = value
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    if (
                        name.isNotBlank() &&
                        phoneNumber.isNotBlank()
                    ) {

                        onSave(
                            name,
                            phoneNumber
                        )
                    }
                }
            ) {

                Text("Guardar")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Cancelar")
            }
        }
    )
}