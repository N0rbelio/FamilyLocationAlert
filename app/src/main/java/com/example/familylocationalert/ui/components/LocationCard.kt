package com.example.familylocationalert.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.familylocationalert.data.LocationPoint
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp

@Composable
fun LocationCard(
    location: LocationPoint,
    onDelete: () -> Unit
) {

    Card(

        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp

        )
    ) {

        Column (
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = location.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Latitude: ${location.latitude}")

            Text("Longitude: ${location.longitude}")

            Text("Raio: ${location.radiusMeters} m")

            Button(
                onClick = onDelete
            ) {
                Text("🗑️ Apagar")
            }
        }
    }
}