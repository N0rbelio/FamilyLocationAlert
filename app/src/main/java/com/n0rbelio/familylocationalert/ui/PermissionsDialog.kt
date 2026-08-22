package com.n0rbelio.familylocationalert.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun PermissionsDialog(
    onContinue: () -> Unit
) {

    AlertDialog(
        onDismissRequest = {
            // Não fechar ao tocar fora.
        },

        title = {
            Text(
                text = "Permissões necessárias"
            )
        },

        text = {
            Text(
                text =
                    "Para funcionar corretamente, a Family Location Alert " +
                            "precisa de algumas permissões.\n\n" +
                            "📍 Localização\n" +
                            "Permite monitorizar a localização e detetar " +
                            "entrada e saída das zonas.\n\n" +
                            "🗺️ Localização em segundo plano\n" +
                            "Permite continuar a monitorização quando a " +
                            "aplicação está fechada.\n\n" +
                            "📱 SMS\n" +
                            "Permite enviar alertas SMS aos contactos configurados."
            )
        },

        confirmButton = {

            Button(
                onClick = onContinue
            ) {
                Text("Continuar")
            }
        }
    )
}