package com.example.familylocationalert.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HeaderSection(
    monitoring: Boolean
) {

    Text(
        text = "Family Location Alert",
        style = MaterialTheme.typography.headlineMedium
    )

    Text(
        text = "Estado",
        style = MaterialTheme.typography.titleMedium
    )

    Text(
        text = if (monitoring)
            "🟢 Monitorização Ativa"
        else
            "🔴 Monitorização Parada",
        style = MaterialTheme.typography.bodyLarge
    )
}