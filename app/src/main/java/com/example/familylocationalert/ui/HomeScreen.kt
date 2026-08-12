package com.example.familylocationalert.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun HomeScreen() {

    val homeViewModel: HomeViewModel = viewModel()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->

        if (granted) {
            homeViewModel.startMonitoring()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Family Location Alert",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("Estado:")
        Text(
            if (homeViewModel.monitoring)
                "🟢 Monitorização Ativa"
            else
                "🔴 Parado"
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (homeViewModel.monitoring) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Latitude:")
            Text(
                homeViewModel.latitude?.toString() ?: "A obter localização..."
            )

            Text("Longitude:")
            Text(
                homeViewModel.longitude?.toString() ?: "A obter localização..."
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        ) {
            Text("Iniciar Monitorização")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                homeViewModel.stopMonitoring()
            }
        ) {
            Text("Parar Monitorização")
        }
    }
}