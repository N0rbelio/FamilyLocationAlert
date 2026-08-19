package com.n0rbelio.familylocationalert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.n0rbelio.familylocationalert.ui.AddLocationScreen
import com.n0rbelio.familylocationalert.ui.AdminScreen
import com.n0rbelio.familylocationalert.ui.ContactsScreen
import com.n0rbelio.familylocationalert.ui.HomeScreen
import com.n0rbelio.familylocationalert.ui.LocationsScreen
import com.n0rbelio.familylocationalert.ui.theme.FamilyLocationAlertTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.n0rbelio.familylocationalert.ui.HomeViewModel
import com.n0rbelio.familylocationalert.data.LocationPoint
import com.n0rbelio.familylocationalert.ui.EditLocationScreen
import com.n0rbelio.familylocationalert.ui.ContactViewModel
import androidx.activity.compose.BackHandler

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            FamilyLocationAlertTheme {

                var adminMode by remember {
                    mutableStateOf(false)
                }

                var contactsMode by remember {
                    mutableStateOf(false)
                }

                var locationsMode by remember {
                    mutableStateOf(false)
                }

                var addLocationMode by remember {
                    mutableStateOf(false)
                }

                var editLocation by remember {
                    mutableStateOf<LocationPoint?>(null)
                }

                val homeViewModel: HomeViewModel = viewModel()

                val contactViewModel: ContactViewModel = viewModel()


                BackHandler {

                    when {

                        editLocation != null -> {
                            editLocation = null
                        }

                        addLocationMode -> {
                            addLocationMode = false
                        }

                        contactsMode -> {
                            contactsMode = false
                        }

                        locationsMode -> {
                            locationsMode = false
                        }

                        adminMode -> {
                            adminMode = false
                        }

                        else -> {
                            finish()
                        }
                    }
                }


                when {

                    // HOME
                    !adminMode -> {

                        HomeScreen(
                            onAdminClick = {
                                adminMode = true
                            }
                        )
                    }

                    // CONTACTOS
                    contactsMode -> {

                        ContactsScreen(
                            onBackClick = {
                                contactsMode = false
                            }
                        )
                    }

                    editLocation != null -> {

                        EditLocationScreen(

                            location = editLocation!!,

                            contacts = contactViewModel.contacts,

                            currentLatitude = homeViewModel.latitude,

                            currentLongitude = homeViewModel.longitude,

                            selectedContactIds =
                                homeViewModel.selectedContactIds,

                            onBackClick = {
                                editLocation = null
                            },

                            onSave = {
                                    id,
                                    name,
                                    latitude,
                                    longitude,
                                    radius,
                                    contactIds,
                                    smsAlertMode,
                                    trackTime ->

                                homeViewModel.updateLocation(
                                    id = id,
                                    name = name,
                                    latitude = latitude,
                                    longitude = longitude,
                                    radiusMeters = radius,
                                    smsAlertMode = smsAlertMode,
                                    trackTime = trackTime
                                )

                                homeViewModel.saveLocationContacts(
                                    locationId = id,
                                    contactIds = contactIds
                                )

                                editLocation = null
                            }
                        )
                    }

                    // NOVA ZONA
                    addLocationMode -> {

                        AddLocationScreen(
                            contacts = contactViewModel.contacts,

                            onBackClick = {
                                addLocationMode = false
                            },

                            onSave = {
                                    name,
                                    latitude,
                                    longitude,
                                    radius,
                                    contactIds,
                                    smsAlertMode,
                                    trackTime ->

                                homeViewModel.saveLocation(
                                    name = name,
                                    latitude = latitude,
                                    longitude = longitude,
                                    radiusMeters = radius,
                                    contactIds = contactIds,
                                    smsAlertMode = smsAlertMode,
                                    trackTime = trackTime
                                )

                                addLocationMode = false
                            }
                        )
                    }

                    // ZONAS
                    locationsMode -> {

                        LocationsScreen(

                            onBackClick = {
                                locationsMode = false
                            },

                            onAddLocationClick = {
                                addLocationMode = true
                            },

                            onEditLocationClick = { location ->

                                homeViewModel.loadContactsForLocation(
                                    location.id
                                )

                                editLocation = location
                            }
                        )
                    }

                    // ADMIN
                    else -> {

                        AdminScreen(

                            onBackClick = {
                                adminMode = false
                            },

                            onContactsClick = {
                                contactsMode = true
                            },

                            onLocationsClick = {
                                locationsMode = true
                            }
                        )
                    }
                }
            }
        }
    }
}
