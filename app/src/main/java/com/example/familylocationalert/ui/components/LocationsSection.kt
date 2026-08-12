package com.example.familylocationalert.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familylocationalert.data.LocationPoint

@Composable
fun LocationsSection(

    locations: List<LocationPoint>,

    onDelete: (LocationPoint) -> Unit

) {

    if (locations.isEmpty()) {

        Text(
            text = "Nenhum local configurado.",
            style = MaterialTheme.typography.bodyMedium
        )

        return
    }

    Text(
        text = "Locais Configurados",
        style = MaterialTheme.typography.titleLarge
    )

    Spacer(modifier = Modifier.height(8.dp))

    locations.forEach { location ->

        LocationCard(
            location = location,
            onDelete = {
                onDelete(location)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}