package com.n0rbelio.familylocationalert.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TestSection(
    hasLocations: Boolean,
    result: String,
    onInside: () -> Unit,
    onOutside: () -> Unit
) {

    if (!hasLocations) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "🧪 Testes",
            style = MaterialTheme.typography.titleMedium
        )

        Button(
            onClick = onInside,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📍 Simular DENTRO")
        }

        Button(
            onClick = onOutside,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🚶 Simular FORA")
        }

        if (result.isNotBlank()) {

            Text(
                text = result,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}