package com.n0rbelio.familylocationalert.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
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
import com.n0rbelio.familylocationalert.data.Contact

@Composable
fun ContactSelectionDialog(
    contacts: List<Contact>,
    selectedContactIds: Set<String>,
    onDismiss: () -> Unit,
    onSave: (Set<String>) -> Unit
) {

    var selectedIds by remember {
        mutableStateOf(selectedContactIds)
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Selecionar contactos")
        },

        text = {

            if (contacts.isEmpty()) {

                Text(
                    text = "Ainda não existem contactos."
                )

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    items(
                        items = contacts,
                        key = { it.id }
                    ) { contact ->

                        val isSelected =
                            contact.id in selectedIds

                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 4.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = isSelected,

                                onCheckedChange = {

                                    selectedIds =
                                        if (isSelected) {

                                            selectedIds -
                                                    contact.id

                                        } else {

                                            selectedIds +
                                                    contact.id
                                        }
                                }
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {

                                Text(
                                    text = contact.name
                                )

                                Text(
                                    text =
                                        contact.phoneNumber
                                )
                            }
                        }
                    }
                }
            }
        },

        confirmButton = {

            TextButton(
                onClick = {
                    onSave(selectedIds)
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