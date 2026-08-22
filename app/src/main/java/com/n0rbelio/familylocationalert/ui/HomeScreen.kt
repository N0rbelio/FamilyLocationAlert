package com.n0rbelio.familylocationalert.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.Priority
import com.n0rbelio.familylocationalert.ui.theme.StatusActive
import com.n0rbelio.familylocationalert.ui.theme.StatusInactive

@Composable
fun HomeScreen(
    onAdminClick: () -> Unit = {}
) {

    val homeViewModel: HomeViewModel = viewModel()

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    val active = homeViewModel.monitoring
    val latitude = homeViewModel.latitude
    val longitude = homeViewModel.longitude

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            )
    ) {

        // ─────────────────────────────────────
        // HEADER
        // ─────────────────────────────────────

        Text(
            text = "Family Location Alert",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(
                Alignment.TopStart
            )
        )

        // ─────────────────────────────────────
        // DASHBOARD
        // ─────────────────────────────────────

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            // ─────────────────────────────────
            // ESTADO
            // ─────────────────────────────────

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) {
                            StatusActive
                        } else {
                            StatusInactive
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = if (active) "✓" else "×",
                    style =
                        MaterialTheme.typography.headlineLarge,
                    color =
                        MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = if (active) {
                    "Monitorização ativa"
                } else {
                    "Monitorização inativa"
                },
                style =
                    MaterialTheme.typography.headlineSmall,
                color =
                    MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.size(24.dp)
            )

            // ─────────────────────────────────
            // LOCALIZAÇÃO ATUAL
            // ─────────────────────────────────

            Text(
                text =
                    if (
                        latitude != null &&
                        longitude != null
                    ) {
                        "📍 $latitude, $longitude"
                    } else {
                        "📍 Localização desconhecida"
                    },
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.size(16.dp)
            )

            // ─────────────────────────────────
            // MODO DE LOCALIZAÇÃO
            // ─────────────────────────────────

            if (active) {

                Text(
                    text =
                        if (
                            homeViewModel.locationPriority ==
                            Priority.PRIORITY_HIGH_ACCURACY
                        ) {
                            "🎯 Precisão: Alta"
                        } else {
                            "🔋 Precisão: Poupança de energia"
                        },
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ─────────────────────────────────────
        // MENU
        // ─────────────────────────────────────

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {

            IconButton(
                onClick = {
                    menuExpanded = true
                }
            ) {

                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint =
                        MaterialTheme.colorScheme.onBackground
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Modo Admin",
                            color =
                                MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onAdminClick()
                    }
                )
            }
        }
    }
}