package com.example.familylocationalert.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.familylocationalert.permissions.PermissionManager
import com.example.familylocationalert.ui.components.AddLocationSection
import com.example.familylocationalert.ui.components.HeaderSection
import com.example.familylocationalert.ui.components.LocationsSection
import com.example.familylocationalert.ui.components.MonitoringSection
import com.example.familylocationalert.ui.components.TestSection
import androidx.compose.foundation.layout.statusBarsPadding
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

@Composable
fun HomeScreen() {

    val homeViewModel: HomeViewModel = viewModel()

    val context = LocalContext.current
    val permissionManager = PermissionManager(context)

    var configuring by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("200") }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                homeViewModel.startMonitoring()
            }
        }


    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                if (
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )

                } else {

                    homeViewModel.startMonitoring()
                }
            }
        }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 60.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {

            HeaderSection(
                monitoring = homeViewModel.monitoring
            )

        }

        item {

            MonitoringSection(

                monitoring = homeViewModel.monitoring,

                latitude = homeViewModel.latitude,
                longitude = homeViewModel.longitude,

                onStart = {

                    if (!permissionManager.hasLocationPermission()) {

                        permissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )

                    } else if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        notificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )

                    } else {

                        homeViewModel.startMonitoring()
                    }

                },

                onStop = {
                    homeViewModel.stopMonitoring()
                }

            )

        }


        item {

            AddLocationSection(

                configuring = configuring,

                name = name,
                latitude = latitude,
                longitude = longitude,
                radius = radius,

                onConfigureClick = {
                    configuring = !configuring
                },

                onNameChange = {
                    name = it
                },

                onLatitudeChange = {
                    latitude = it
                },

                onLongitudeChange = {
                    longitude = it
                },

                onRadiusChange = {
                    radius = it
                },

                onSaveClick = {

                    val latitudeValue = latitude.toDoubleOrNull()
                    val longitudeValue = longitude.toDoubleOrNull()
                    val radiusValue = radius.toFloatOrNull()

                    if (
                        name.isNotBlank() &&
                        latitudeValue != null &&
                        longitudeValue != null &&
                        radiusValue != null
                    ) {

                        homeViewModel.saveLocation(
                            name,
                            latitudeValue,
                            longitudeValue,
                            radiusValue
                        )

                        configuring = false

                        name = ""
                        latitude = ""
                        longitude = ""
                        radius = "200"

                    }

                }

            )

        }


        item {

            TestSection(

                hasLocations = homeViewModel.locations.isNotEmpty(),

                result = homeViewModel.testResult,

                onInside = {

                    homeViewModel.locations.firstOrNull()?.let { location ->

                        homeViewModel.simulateLocation(
                            location.latitude,
                            location.longitude
                        )

                    }

                },

                onOutside = {

                    homeViewModel.locations.firstOrNull()?.let { location ->

                        homeViewModel.simulateLocation(
                            latitude = location.latitude + 0.01,
                            longitude = location.longitude
                        )

                    }

                }

            )

        }


        item {

            LocationsSection(

                locations = homeViewModel.locations,

                onDelete = {
                    homeViewModel.deleteLocation(it)
                }

            )

        }

    }
}