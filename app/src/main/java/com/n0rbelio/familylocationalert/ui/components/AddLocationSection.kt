package com.n0rbelio.familylocationalert.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddLocationSection(

    configuring: Boolean,

    name: String,
    latitude: String,
    longitude: String,
    radius: String,

    onConfigureClick: () -> Unit,

    onNameChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onRadiusChange: (String) -> Unit,

    onSaveClick: () -> Unit

) {

    Button(
        onClick = onConfigureClick
    ) {
        Text("⚙️ Configurar Local")
    }

    if (!configuring) return

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Novo Local",
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = {
            Text("Nome")
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = latitude,
        onValueChange = onLatitudeChange,
        label = {
            Text("Latitude")
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = longitude,
        onValueChange = onLongitudeChange,
        label = {
            Text("Longitude")
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = radius,
        onValueChange = onRadiusChange,
        label = {
            Text("Raio (m)")
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onSaveClick
    ) {
        Text("Guardar Local")
    }
}