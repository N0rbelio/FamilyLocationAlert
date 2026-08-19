package com.n0rbelio.familylocationalert.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.n0rbelio.familylocationalert.data.LocationPoint
import com.n0rbelio.familylocationalert.data.Contact
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.n0rbelio.familylocationalert.data.SmsAlertMode
import androidx.compose.material3.Switch




@Composable
fun EditLocationScreen(
    location: LocationPoint,
    contacts: List<Contact>,
    selectedContactIds: Set<String>,
    currentLatitude: Double?,
    currentLongitude: Double?,
    onBackClick: () -> Unit,
    onSave: (
        id: String,
        name: String,
        latitude: Double,
        longitude: Double,
        radius: Float,
        contactIds: Set<String>,
        smsAlertMode: SmsAlertMode,
        trackTime: Boolean

    ) -> Unit
) {


    val context = LocalContext.current

    val homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    var gettingLocation by remember {
        mutableStateOf(false)
    }

    var locationError by remember {
        mutableStateOf(false)
    }

    var name by remember {
        mutableStateOf(location.name)
    }

    var latitude by remember {
        mutableStateOf(location.latitude.toString())
    }

    var longitude by remember {
        mutableStateOf(location.longitude.toString())
    }

    var radius by remember {
        mutableStateOf(location.radiusMeters.toString())
    }

    var currentSelectedContactIds by remember {
        mutableStateOf(selectedContactIds)
    }

    var showContactsDialog by remember {
        mutableStateOf(false)
    }

    var smsAlertMode by remember {
        mutableStateOf(location.smsAlertMode)
    }

    var showSmsAlertDialog by remember {
        mutableStateOf(false)
    }

    var trackTime by remember {
        mutableStateOf(location.trackTime)
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true ||
                        permissions[
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ] == true

            if (granted) {

                gettingLocation = true
                locationError = false

                homeViewModel.getCurrentLocation(
                    onResult = { lat, lon ->

                        latitude = lat.toString()
                        longitude = lon.toString()

                        gettingLocation = false
                        locationError = false
                    },
                    onError = {

                        gettingLocation = false
                        locationError = true
                    }
                )

            } else {

                gettingLocation = false
                locationError = true
            }
        }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 15.dp,
                vertical = 9.dp
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

            Text(
                text = "Editar zona",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(
            modifier = Modifier.size(32.dp)
        )

        // NOME

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Nome da zona")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        // LATITUDE

        OutlinedTextField(
            value = latitude,
            onValueChange = {
                latitude = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Latitude")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        // LONGITUDE

        OutlinedTextField(
            value = longitude,
            onValueChange = {
                longitude = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Longitude")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        // RAIO

        OutlinedTextField(
            value = radius,
            onValueChange = {
                radius = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Raio (metros)")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "Contactos dos alertas",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.size(8.dp)
        )

        val selectedContacts =
            contacts.filter {
                it.id in currentSelectedContactIds
            }

        if (selectedContacts.isEmpty()) {

            Text(
                text = "Nenhum contacto selecionado.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        } else {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                selectedContacts.forEach { contact ->

                    Text(
                        text = "👤 ${contact.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }


        Spacer(
            modifier = Modifier.size(8.dp)
        )

        Button(
            onClick = {
                showContactsDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Selecionar contactos")
        }

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = "Tipo de alerta SMS",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.size(8.dp)
        )

        Text(
            text = when (smsAlertMode) {
                SmsAlertMode.BOTH -> "Entrada e saída"
                SmsAlertMode.ENTRY_ONLY -> "Apenas entrada"
                SmsAlertMode.EXIT_ONLY -> "Apenas saída"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.size(8.dp)
        )

        Button(
            onClick = {
                showSmsAlertDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alterar")
        }


        // LOCALIZAÇÃO ATUAL

        Button(
            onClick = {

                val fineGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                val coarseGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                if (fineGranted || coarseGranted) {

                    gettingLocation = true
                    locationError = false

                    homeViewModel.getCurrentLocation(
                        onResult = { lat, lon ->

                            latitude = lat.toString()
                            longitude = lon.toString()

                            gettingLocation = false
                            locationError = false
                        },
                        onError = {

                            gettingLocation = false
                            locationError = true
                        }
                    )

                } else {

                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !gettingLocation
        ) {

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text(
                text = if (gettingLocation) {
                    "A obter localização..."
                } else {
                    "Usar localização atual"
                }
            )
        }

        if (locationError) {

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text(
                text = "Não foi possível obter a localização atual.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Contar tempo nesta zona",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = if (trackTime) {
                        "O tempo será contabilizado enquanto estiveres nesta zona."
                    } else {
                        "Esta zona não participa na contagem de tempo."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = trackTime,
                onCheckedChange = {
                    trackTime = it
                }
            )
        }



        Spacer(
            modifier = Modifier.size(16.dp)
        )

        // GUARDAR

        Button(
            onClick = {

                val latitudeValue =
                    latitude.toDoubleOrNull()

                val longitudeValue =
                    longitude.toDoubleOrNull()

                val radiusValue =
                    radius.toFloatOrNull()

                if (
                    name.isNotBlank() &&
                    latitudeValue != null &&
                    longitudeValue != null &&
                    radiusValue != null &&
                    radiusValue > 0
                ) {

                    onSave(
                        location.id,
                        name.trim(),
                        latitudeValue,
                        longitudeValue,
                        radiusValue,
                        currentSelectedContactIds,
                        smsAlertMode,
                        trackTime
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Guardar alterações")
        }




    }

    if (showContactsDialog) {

        ContactSelectionDialog(

            contacts = contacts,

            selectedContactIds =
                currentSelectedContactIds,

            onDismiss = {
                showContactsDialog = false
            },

            onSave = { selectedIds ->

                currentSelectedContactIds = selectedIds

                showContactsDialog = false
            }
        )
    }








    if (showSmsAlertDialog) {

        SmsAlertSelectionDialog(

            selectedMode = smsAlertMode,

            onDismiss = {
                showSmsAlertDialog = false
            },

            onSave = { mode ->

                smsAlertMode = mode
                showSmsAlertDialog = false
            }
        )
    }
}