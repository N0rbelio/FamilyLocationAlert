package com.n0rbelio.familylocationalert.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.Priority
import com.n0rbelio.familylocationalert.ui.theme.StatusActive
import com.n0rbelio.familylocationalert.ui.theme.StatusInactive

@Composable
fun DevTestesScreen(
    onBack: () -> Unit = {}
) {

    val homeViewModel: HomeViewModel = viewModel()

    val active = homeViewModel.monitoring
    val latitude = homeViewModel.latitude
    val longitude = homeViewModel.longitude
    val priority = homeViewModel.locationPriority
    val testResult = homeViewModel.testResult

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 15.dp,
                vertical = 2.dp
            )
    ) {

        // ─────────────────────────────────────
        // HEADER
        // ─────────────────────────────────────

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Icon(
                imageVector = Icons.Default.DeveloperBoard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Text(
                text = "Dev e Testes",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(
            modifier = Modifier.size(28.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {

            // ─────────────────────────────────
            // MONITORIZAÇÃO
            // ─────────────────────────────────

            Text(
                text = "Monitorização",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint =
                                if (active) {
                                    StatusActive
                                } else {
                                    StatusInactive
                                },
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier = Modifier.size(12.dp)
                        )

                        Column {

                            Text(
                                text =
                                    if (active) {
                                        "Monitorização ativa"
                                    } else {
                                        "Monitorização inativa"
                                    },
                                style =
                                    MaterialTheme.typography.titleMedium,
                                color =
                                    MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(
                                modifier = Modifier.size(2.dp)
                            )

                            Text(
                                text =
                                    if (active) {
                                        "O serviço está a monitorizar a localização."
                                    } else {
                                        "O serviço de localização está parado."
                                    },
                                style =
                                    MaterialTheme.typography.bodyMedium,
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.size(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Button(
                            onClick = {
                                homeViewModel.startMonitoring()
                            },
                            modifier =
                                Modifier.weight(1f),
                            enabled = !active
                        ) {

                            Text("Iniciar")
                        }

                        Button(
                            onClick = {
                                homeViewModel.stopMonitoring()
                            },
                            modifier =
                                Modifier.weight(1f),
                            enabled = active
                        ) {

                            Text("Parar")
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.size(20.dp)
            )

            // ─────────────────────────────────
            // LOCALIZAÇÃO
            // ─────────────────────────────────

            Text(
                text = "Localização atual",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.LocationOn,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(
                        modifier = Modifier.size(12.dp)
                    )

                    Column {

                        if (
                            latitude != null &&
                            longitude != null
                        ) {

                            Text(
                                text = "Latitude: $latitude",
                                style =
                                    MaterialTheme.typography.bodyMedium,
                                color =
                                    MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Longitude: $longitude",
                                style =
                                    MaterialTheme.typography.bodyMedium,
                                color =
                                    MaterialTheme.colorScheme.onSurface
                            )

                        } else {

                            Text(
                                text = "Localização desconhecida",
                                style =
                                    MaterialTheme.typography.bodyMedium,
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.size(20.dp)
            )

// ─────────────────────────────────────
// PRIORIDADE DE LOCALIZAÇÃO
// ─────────────────────────────────────

            if (active) {

                Text(
                    text = "Prioridade de localização",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(
                    modifier = Modifier.size(12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surface
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Speed,
                            contentDescription = null,
                            tint =
                                MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier = Modifier.size(12.dp)
                        )

                        Column {

                            Text(
                                text = when (priority) {

                                    Priority.PRIORITY_HIGH_ACCURACY ->
                                        "Alta precisão"

                                    Priority.PRIORITY_BALANCED_POWER_ACCURACY ->
                                        "Equilibrada / poupança de energia"

                                    Priority.PRIORITY_LOW_POWER ->
                                        "Baixo consumo"

                                    Priority.PRIORITY_PASSIVE ->
                                        "Passiva"

                                    else ->
                                        "Desconhecida"
                                },
                                style =
                                    MaterialTheme.typography.titleMedium,
                                color =
                                    MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(
                                modifier = Modifier.size(2.dp)
                            )

                            Text(
                                text =
                                    "Modo atualmente utilizado pelo serviço.",
                                style =
                                    MaterialTheme.typography.bodyMedium,
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.size(20.dp)
                )
            }
            // ─────────────────────────────────
            // TESTES
            // ─────────────────────────────────

            Text(
                text = "Testes de localização",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.GpsFixed,
                            contentDescription = null,
                            tint =
                                MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier = Modifier.size(12.dp)
                        )

                        Column {

                            Text(
                                text = "Simulação",
                                style =
                                    MaterialTheme.typography.titleMedium,
                                color =
                                    MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(
                                modifier = Modifier.size(2.dp)
                            )

                            Text(
                                text =
                                    "Testa a entrada e saída das zonas.",
                                style =
                                    MaterialTheme.typography.bodyMedium,
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.size(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Button(
                            onClick = {

                                homeViewModel.simulateLocation(
                                    latitude = 40.8725,
                                    longitude = -8.6125786
                                )
                            },
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text("Dentro")
                        }

                        Button(
                            onClick = {

                                homeViewModel.simulateLocation(
                                    latitude = 40.8755,
                                    longitude = -8.6125786
                                )
                            },
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text("Fora")
                        }
                    }

                    Spacer(
                        modifier = Modifier.size(8.dp)
                    )

                    Button(
                        onClick = {
                            homeViewModel.resumeRealLocation()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("Retomar GPS real")
                    }
                }
            }

            Spacer(
                modifier = Modifier.size(20.dp)
            )

            // ─────────────────────────────────
            // RESULTADO
            // ─────────────────────────────────

            Text(
                text = "Resultado",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {

                Text(
                    text =
                        if (testResult.isNotBlank()) {
                            testResult
                        } else {
                            "Nenhum teste executado."
                        },
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        if (testResult.isNotBlank()) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                )
            }

            Spacer(
                modifier = Modifier.size(24.dp)
            )

        }
    }
}