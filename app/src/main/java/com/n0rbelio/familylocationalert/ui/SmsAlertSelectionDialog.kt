package com.n0rbelio.familylocationalert.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
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
import com.n0rbelio.familylocationalert.data.SmsAlertMode


@Composable
fun SmsAlertSelectionDialog(
    selectedMode: SmsAlertMode,
    onDismiss: () -> Unit,
    onSave: (SmsAlertMode) -> Unit
) {

    var currentMode by remember {
        mutableStateOf(selectedMode)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Tipo de alerta SMS")
        },

        text = {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                SmsAlertOption(
                    text = "Entrada e saída",
                    selected = currentMode == SmsAlertMode.BOTH,
                    onClick = {
                        currentMode = SmsAlertMode.BOTH
                    }
                )

                SmsAlertOption(
                    text = "Apenas entrada",
                    selected = currentMode == SmsAlertMode.ENTRY_ONLY,
                    onClick = {
                        currentMode = SmsAlertMode.ENTRY_ONLY
                    }
                )

                SmsAlertOption(
                    text = "Apenas saída",
                    selected = currentMode == SmsAlertMode.EXIT_ONLY,
                    onClick = {
                        currentMode = SmsAlertMode.EXIT_ONLY
                    }
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    onSave(currentMode)
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

@Composable
private fun SmsAlertOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(
                vertical = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Spacer(
            modifier = Modifier.size(8.dp)
        )

        Text(text)
    }
}