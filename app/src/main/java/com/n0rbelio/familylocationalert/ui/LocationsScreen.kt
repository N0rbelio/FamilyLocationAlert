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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.filled.DeveloperBoard
import com.n0rbelio.familylocationalert.data.LocationPoint
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import com.n0rbelio.familylocationalert.data.Contact
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import com.n0rbelio.familylocationalert.data.SmsAlertMode

@Composable
fun LocationsScreen(
    onBackClick: () -> Unit,
    onAddLocationClick: () -> Unit,
    onEditLocationClick: (LocationPoint) -> Unit
) {

    val homeViewModel: HomeViewModel = viewModel()

    val locationContacts = remember {
        mutableStateMapOf<String, List<Contact>>()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                horizontal = 15.dp,
                vertical = 2.dp
            )
    ) {

        // HEADER

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Text(
                text = "Zonas",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            if (homeViewModel.locations.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = "Ainda não existem zonas.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(
                        modifier = Modifier.size(8.dp)
                    )

                    Text(
                        text = "Adicione uma zona para começar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = homeViewModel.locations,
                        key = { it.id }

                    ) { location ->

                        LaunchedEffect(location.id) {

                            locationContacts[location.id] =
                                homeViewModel.getContactsForLocation(
                                    location.id
                                )
                        }

                        val contacts =
                            locationContacts[location.id] ?: emptyList()



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
                                    .padding(16.dp),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint =
                                        MaterialTheme.colorScheme.primary,
                                    modifier =
                                        Modifier.size(32.dp)
                                )

                                Spacer(
                                    modifier = Modifier.size(16.dp)
                                )

                                Column(
                                    modifier =
                                        Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = location.name,
                                        style =
                                            MaterialTheme.typography.titleMedium,
                                        color =
                                            MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(
                                        modifier = Modifier.size(4.dp)
                                    )

                                    Text(
                                        text =
                                            "Raio: ${location.radiusMeters.toInt()} m",
                                        style =
                                            MaterialTheme.typography.bodyMedium,
                                        color =
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text =
                                            "${location.latitude}, ${location.longitude}",
                                        style =
                                            MaterialTheme.typography.bodySmall,
                                        color =
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(
                                        modifier = Modifier.size(12.dp)
                                    )

                                    Text(
                                        text = "Alertas SMS",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        text = when (location.smsAlertMode) {
                                            SmsAlertMode.BOTH ->
                                                "📥 Entrada e saída"

                                            SmsAlertMode.ENTRY_ONLY ->
                                                "📥 Apenas entrada"

                                            SmsAlertMode.EXIT_ONLY ->
                                                "📤 Apenas saída"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (contacts.isEmpty()) {

                                        Text(
                                            text = "⚠️ Nenhum contacto selecionado",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    else {

                                        Column(
                                            verticalArrangement =
                                                Arrangement.spacedBy(4.dp)
                                        ) {

                                            contacts.chunked(2).forEach { rowContacts ->

                                                Row(
                                                    horizontalArrangement =
                                                        Arrangement.spacedBy(6.dp)
                                                ) {

                                                    rowContacts.forEach { contact ->

                                                        AssistChip(
                                                            onClick = {
                                                                // Por enquanto apenas visual.
                                                            },

                                                            label = {
                                                                Text(
                                                                    text = contact.name,
                                                                    style =
                                                                        MaterialTheme.typography.labelMedium
                                                                )
                                                            },

                                                            leadingIcon = {

                                                                Text(
                                                                    text = "👤"
                                                                )
                                                            },

                                                            colors =
                                                                AssistChipDefaults.assistChipColors(
                                                                    containerColor =
                                                                        MaterialTheme.colorScheme.surfaceVariant
                                                                )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        onEditLocationClick(location)
                                    }
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar zona",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        homeViewModel.deleteLocation(
                                            location
                                        )
                                    }
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.Delete,
                                        contentDescription =
                                            "Apagar zona",
                                        tint =
                                            MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.size(16.dp)
            )

            FloatingActionButton(
                onClick = onAddLocationClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar zona"
                )
            }
        }
    }
}
