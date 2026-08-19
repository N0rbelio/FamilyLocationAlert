package com.n0rbelio.familylocationalert.service

import com.n0rbelio.familylocationalert.data.ContactDao
import com.n0rbelio.familylocationalert.data.LocationContactDao
import com.n0rbelio.familylocationalert.data.LocationDao
import com.n0rbelio.familylocationalert.data.SmsAlertMode

class LocationEventProcessor(
    private val notificationManager: LocationNotificationManager,
    private val smsSender: SmsSender,
    private val locationContactDao: LocationContactDao,
    private val contactDao: ContactDao,
    private val locationDao: LocationDao
) {

    suspend fun process(
        statuses: List<LocationStatus>
    ) {

        statuses.forEach { status ->

            when (status.event) {

                LocationEvent.NONE -> Unit

                LocationEvent.ENTERED -> {

                    notificationManager.notifyEntered(
                        status.name
                    )

                    val location =
                        locationDao.getById(status.locationId)


                    if (
                        location?.smsAlertMode == SmsAlertMode.BOTH ||
                        location?.smsAlertMode == SmsAlertMode.ENTRY_ONLY
                    ) {

                        sendSmsToLocationContacts(
                            status,
                            entered = true
                        )
                    }
                }

                LocationEvent.EXITED -> {

                    notificationManager.notifyExited(
                        status.name
                    )

                    val location =
                        locationDao.getById(status.locationId)

                    if (
                        location?.smsAlertMode == SmsAlertMode.BOTH ||
                        location?.smsAlertMode == SmsAlertMode.EXIT_ONLY
                    ) {

                        sendSmsToLocationContacts(
                            status,
                            entered = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun sendSmsToLocationContacts(
        status: LocationStatus,
        entered: Boolean
    ) {

        val contactIds =
            locationContactDao.getContactIdsForLocation(
                status.locationId
            )

        if (contactIds.isEmpty()) {

            println(
                "LocationEventProcessor: " +
                        "Nenhum contacto associado à zona ${status.name}"
            )

            return
        }

        val contacts =
            contactDao
                .getAll()
                .filter {
                    it.id in contactIds
                }

        val message =
            if (entered) {
                "Entrou em ${status.name}"
            } else {
                "Saiu de ${status.name}"
            }

        contacts.forEach { contact ->

            if (contact.phoneNumber.isBlank()) {
                return@forEach
            }

            println(
                "LocationEventProcessor: " +
                        "A enviar SMS para um contacto associado à zona ${contact.name} " +
                        "(${contact.phoneNumber})"
            )

            println(
                "LocationEventProcessor: ${System.currentTimeMillis()} - a enviar SMS"
            )

            smsSender.send(
                contact.phoneNumber,
                message
            )
        }
    }
}