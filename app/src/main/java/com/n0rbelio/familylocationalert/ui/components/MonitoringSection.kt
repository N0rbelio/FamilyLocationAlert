package com.n0rbelio.familylocationalert.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MonitoringSection(

    monitoring: Boolean,

    latitude: Double?,
    longitude: Double?,

    onStart: () -> Unit,
    onStop: () -> Unit

) {

    if (monitoring) {

        Text(
            "Latitude",
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            latitude?.toString() ?: "-"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Longitude",
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            longitude?.toString() ?: "-"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    Button(
        onClick = onStart
    ) {
        Text("Iniciar Monitorização")
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = onStop
    ) {
        Text("Parar Monitorização")
    }
}