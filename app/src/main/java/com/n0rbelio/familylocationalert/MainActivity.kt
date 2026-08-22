package com.n0rbelio.familylocationalert

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.n0rbelio.familylocationalert.data.LocationPoint
import com.n0rbelio.familylocationalert.ui.AddLocationScreen
import com.n0rbelio.familylocationalert.ui.AdminScreen
import com.n0rbelio.familylocationalert.ui.ContactViewModel
import com.n0rbelio.familylocationalert.ui.ContactsScreen
import com.n0rbelio.familylocationalert.ui.DevTestesScreen
import com.n0rbelio.familylocationalert.ui.EditLocationScreen
import com.n0rbelio.familylocationalert.ui.HomeScreen
import com.n0rbelio.familylocationalert.ui.HomeViewModel
import com.n0rbelio.familylocationalert.ui.LocationsScreen
import com.n0rbelio.familylocationalert.ui.theme.FamilyLocationAlertTheme
import androidx.core.net.toUri
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity


class MainActivity : FragmentActivity() {

    private var permissionStep by mutableStateOf(
        PermissionStep.NONE
    )

    /**
     * Indica que abrimos as definições especificamente
     * para conceder ACCESS_BACKGROUND_LOCATION.
     */

    /**
     * Evita executar o fluxo inicial várias vezes.
     */

    private fun authenticateAdmin(
        onSuccess: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricManager = BiometricManager.from(this)

        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL

        val canAuthenticate =
            biometricManager.canAuthenticate(authenticators)

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            return
        }

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    onSuccess()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(
                        errorCode,
                        errString
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            }
        )

        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticação necessária")
                .setSubtitle("Acesso à área de administração")
                .setAllowedAuthenticators(authenticators)
                .build()

        biometricPrompt.authenticate(promptInfo)
    }

    // =========================================================
    // PERMISSION LAUNCHER
    // =========================================================

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->

            val fineGranted =
                result[Manifest.permission.ACCESS_FINE_LOCATION] == true

            if (fineGranted) {
                checkNextPermission()
            } else {
                permissionStep =
                    PermissionStep.LOCATION_DENIED
            }
        }


    private val smsPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                checkNextPermission()
            } else {
                permissionStep =
                    PermissionStep.SMS_DENIED
            }
        }


    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                checkNextPermission()
            } else {
                permissionStep =
                    PermissionStep.NOTIFICATIONS_DENIED
            }
        }


    // =========================================================
    // ACTIVITY
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {

            FamilyLocationAlertTheme {

                PermissionManager()

                AppContent()
            }
        }

        checkNextPermission()
    }


    override fun onResume() {
        super.onResume()

        // Não fazer check automático aqui.
        //
        // Quando o utilizador voltar das definições,
        // o diálogo BACKGROUND_SETTINGS continua visível.
        //
        // O utilizador terá de carregar em "Já permiti".
    }


    // =========================================================
    // VERIFICAR PERMISSÕES
    // =========================================================

    private fun checkNextPermission() {

        val fineLocation =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


        val coarseLocation =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


        val backgroundLocation =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            } else {
                true
            }


        val sms =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED


        val notifications =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            } else {
                true
            }


        when {

            /*
             * IMPORTANTE:
             *
             * Não basta COARSE.
             * A aplicação exige FINE.
             */
            !fineLocation -> {

                permissionStep =
                    PermissionStep.LOCATION
            }


            /*
             * Android 10+:
             * localização em segundo plano.
             */
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    !backgroundLocation -> {

                permissionStep =
                    PermissionStep.BACKGROUND_LOCATION
            }


            /*
             * SMS.
             */
            !sms -> {

                permissionStep =
                    PermissionStep.SMS
            }


            /*
             * Notificações.
             */
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    !notifications -> {

                permissionStep =
                    PermissionStep.NOTIFICATIONS
            }


            /*
             * Tudo concedido.
             */
            else -> {

                permissionStep =
                    PermissionStep.NONE
            }
        }
    }


    // =========================================================
    // PEDIR LOCALIZAÇÃO
    // =========================================================

    private fun requestLocation() {

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }


    // =========================================================
    // PEDIR SMS
    // =========================================================

    private fun requestSms() {

        smsPermissionLauncher.launch(
            Manifest.permission.SEND_SMS
        )
    }


    // =========================================================
    // PEDIR NOTIFICAÇÕES
    // =========================================================

    private fun requestNotifications() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )

        } else {

            checkNextPermission()
        }
    }


    // =========================================================
    // ABRIR DEFINIÇÕES DA LOCALIZAÇÃO
    // =========================================================

    private fun openBackgroundLocationSettings() {

        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        ).apply {
            data = "package:$packageName".toUri()
        }

        startActivity(intent)
    }

    // =========================================================
    // UI DAS PERMISSÕES
    // =========================================================

    @Composable
    private fun PermissionManager() {

        when (permissionStep) {

            // =========================================================
            // LOCALIZAÇÃO
            // =========================================================

            PermissionStep.LOCATION -> {

                AlertDialog(
                    onDismissRequest = {},

                    title = {
                        Text("📍 Permissão de localização")
                    },

                    text = {
                        Text(
                            "A Family Location Alert precisa de acesso à localização " +
                                    "para funcionar corretamente.\n\n" +

                                    "A localização é utilizada para:\n\n" +

                                    "• Saber onde o dispositivo se encontra\n" +
                                    "• Detetar entrada em zonas configuradas\n" +
                                    "• Detetar saída das zonas\n" +
                                    "• Gerar alertas de localização\n" +
                                    "• Continuar a monitorização em segundo plano\n\n" +

                                    "É necessária a localização PRECISA.\n\n" +

                                    "Na próxima janela do Android, seleciona " +
                                    "\"Permitir enquanto usa a aplicação\" " +
                                    "e mantém a opção de localização precisa ativada."
                        )
                    },

                    confirmButton = {
                        Button(
                            onClick = {

                                permissionStep =
                                    PermissionStep.NONE

                                requestLocation()
                            }
                        ) {
                            Text("Continuar")
                        }
                    }
                )
            }


            // =========================================================
            // LOCALIZAÇÃO NEGADA
            // =========================================================

            PermissionStep.LOCATION_DENIED -> {

                AlertDialog(
                    onDismissRequest = {},

                    title = {
                        Text("⚠️ Localização necessária")
                    },

                    text = {
                        Text(
                            "A Family Location Alert não consegue funcionar " +
                                    "corretamente sem acesso à localização precisa.\n\n" +

                                    "Esta permissão é necessária para:\n\n" +

                                    "• Detetar a localização do dispositivo\n" +
                                    "• Detetar entrada nas zonas\n" +
                                    "• Detetar saída das zonas\n" +
                                    "• Ativar a monitorização de localização\n\n" +

                                    "Sem esta permissão, estas funcionalidades " +
                                    "não estarão disponíveis.\n\n" +

                                    "Seleciona \"Permitir\" na próxima janela do Android."
                        )
                    },

                    confirmButton = {
                        Button(
                            onClick = {

                                permissionStep =
                                    PermissionStep.NONE

                                requestLocation()
                            }
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                )
            }


            // =========================================================
            // LOCALIZAÇÃO EM SEGUNDO PLANO
            // =========================================================

            PermissionStep.BACKGROUND_LOCATION -> {

                AlertDialog(
                    onDismissRequest = {},

                    title = {
                        Text("📍 Localização em segundo plano")
                    },

                    text = {
                        Text(
                            "A Family Location Alert precisa de uma permissão " +
                                    "adicional para continuar a monitorizar a localização " +
                                    "quando a aplicação não está aberta.\n\n" +

                                    "Esta permissão permite:\n\n" +

                                    "• Continuar a monitorizar a localização\n" +
                                    "• Detetar entradas nas zonas\n" +
                                    "• Detetar saídas das zonas\n" +
                                    "• Gerar os alertas configurados\n\n" +

                                    "O Android não permite conceder esta permissão " +
                                    "através da janela normal de permissões.\n\n" +

                                    "Vamos abrir as definições da aplicação.\n\n" +

                                    "Procura a opção de localização e seleciona:\n\n" +

                                    "\"Permitir sempre\""
                        )
                    },

                    confirmButton = {
                        Button(
                            onClick = {

                                permissionStep =
                                    PermissionStep.BACKGROUND_SETTINGS

                                openBackgroundLocationSettings()
                            }
                        ) {
                            Text("Abrir definições")
                        }
                    }
                )
            }


            // =========================================================
            // VOLTOU DAS DEFINIÇÕES
            // =========================================================

            PermissionStep.BACKGROUND_SETTINGS -> {

                AlertDialog(
                    onDismissRequest = {},

                    title = {
                        Text("📍 Confirmar localização")
                    },

                    text = {
                        Text(
                            "Para a Family Location Alert funcionar " +
                                    "em segundo plano é necessário selecionar:\n\n" +

                                    "\"Permitir sempre\"\n\n" +

                                    "Volta às definições da aplicação e confirma " +
                                    "que essa opção está selecionada.\n\n" +

                                    "Se já concedeste a permissão, seleciona " +
                                    "\"Continuar\"."
                        )
                    },

                    confirmButton = {
                        Button(
                            onClick = {
                                checkNextPermission()
                            }
                        ) {
                            Text("Continuar")
                        }
                    }
                )
            }


            // =========================================================
            // SMS
            // =========================================================

            PermissionStep.SMS -> {

                AlertDialog(
                    onDismissRequest = {},

                    title = {
                        Text("📱 Permissão para enviar SMS")
                    },

                    text = {
                        Text(
                            "A Family Location Alert pode enviar SMS " +
                                    "automaticamente quando ocorrerem eventos " +
                                    "de localização configurados.\n\n" +

                                    "Os SMS são enviados através do cartão SIM " +
                                    "deste dispositivo.\n\n" +

                                    "Esta permissão é necessária para utilizar " +
                                    "os alertas por SMS.\n\n" +

                                    "Dependendo do tarifário, o envio de SMS " +
                                    "pode ter custos."
                        )
                    },

                    confirmButton = {
                        Button(
                            onClick = {

                                permissionStep =
                                    PermissionStep.NONE

                                requestSms()
                            }
                        ) {
                            Text("Permitir SMS")
                        }
                    }
                )
            }


            // =========================================================
            // SMS NEGADO
            // =========================================================

            PermissionStep.SMS_DENIED -> {

                AlertDialog(
                    onDismissRequest = {},

                    title = {
                        Text("⚠️ Permissão de SMS necessária")
                    },

                    text = {
                        Text(
                            "A Family Location Alert não poderá enviar " +
                                    "os alertas por SMS sem esta permissão.\n\n" +

                                    "Os SMS são enviados diretamente através " +
                                    "do cartão SIM deste dispositivo.\n\n" +

                                    "Se pretendes utilizar os alertas por SMS, " +
                                    "é necessário conceder esta permissão."
                        )
                    },

                    confirmButton = {
                        Button(
                            onClick = {

                                permissionStep =
                                    PermissionStep.NONE

                                requestSms()
                            }
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                )
            }


            // =========================================================
            // NOTIFICAÇÕES
            // =========================================================

            PermissionStep.NOTIFICATIONS -> {

                AlertDialog(
                    onDismissRequest = {},

                    title = {
                        Text("🔔 Permissão para notificações")
                    },

                    text = {
                        Text(
                            "A Family Location Alert utiliza notificações " +
                                    "para informar o utilizador sobre o estado " +
                                    "da monitorização.\n\n" +

                                    "As notificações também são utilizadas pelo " +
                                    "serviço de localização em segundo plano.\n\n" +

                                    "Esta permissão é necessária para apresentar " +
                                    "as notificações da aplicação."
                        )
                    },

                    confirmButton = {
                        Button(
                            onClick = {

                                permissionStep =
                                    PermissionStep.NONE

                                requestNotifications()
                            }
                        ) {
                            Text("Permitir notificações")
                        }
                    }
                )
            }


            // =========================================================
            // NOTIFICAÇÕES NEGADAS
            // =========================================================

            PermissionStep.NOTIFICATIONS_DENIED -> {

                AlertDialog(
                    onDismissRequest = {},

                    title = {
                        Text("⚠️ Notificações desativadas")
                    },

                    text = {
                        Text(
                            "A Family Location Alert precisa de permissões " +
                                    "de notificação para apresentar o estado da " +
                                    "monitorização e os alertas da aplicação.\n\n" +

                                    "Sem esta permissão, algumas informações e " +
                                    "alertas não poderão ser apresentados."
                        )
                    },

                    confirmButton = {
                        Button(
                            onClick = {

                                permissionStep =
                                    PermissionStep.NONE

                                requestNotifications()
                            }
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                )
            }


            // =========================================================
            // TODAS AS PERMISSÕES CONCEDIDAS
            // =========================================================

            PermissionStep.NONE -> Unit
        } // fecha when(permissionStep)

    } // fecha PermissionManager()


    // =========================================================
    // CONTEÚDO DA APP
    // =========================================================

    @Composable
    private fun AppContent() {

        var adminMode by rememberSaveable {
            mutableStateOf(false)
        }

       var contactsMode by rememberSaveable {
            mutableStateOf(false)
        }

        var locationsMode by rememberSaveable {
            mutableStateOf(false)
        }

        var addLocationMode by rememberSaveable {
            mutableStateOf(false)
        }

        var editLocation by rememberSaveable {
            mutableStateOf<LocationPoint?>(null)
        }

        var devTestesMode by rememberSaveable {
            mutableStateOf(false)
        }

        val homeViewModel: HomeViewModel =
            viewModel()

        val contactViewModel: ContactViewModel =
            viewModel()


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

                devTestesMode -> {
                    devTestesMode = false
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

            // =================================================
            // HOME
            // =================================================

            !adminMode -> {

                HomeScreen(
                    onAdminClick = {
                        authenticateAdmin {
                            adminMode = true
                        }
                    }
                )
            }


            // =================================================
            // DEV / TESTES
            // =================================================

            devTestesMode -> {

                DevTestesScreen(
                    onBack = {
                        devTestesMode = false
                    }
                )
            }


            // =================================================
            // CONTACTOS
            // =================================================

            contactsMode -> {

                ContactsScreen(
                    onBackClick = {
                        contactsMode = false
                    }
                )
            }


            // =================================================
            // EDITAR ZONA
            // =================================================

            editLocation != null -> {

                EditLocationScreen(

                    location = editLocation!!,

                    contacts =
                        contactViewModel.contacts,

                    currentLatitude =
                        homeViewModel.latitude,

                    currentLongitude =
                        homeViewModel.longitude,

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


            // =================================================
            // NOVA ZONA
            // =================================================

            addLocationMode -> {

                AddLocationScreen(

                    contacts =
                        contactViewModel.contacts,

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


            // =================================================
            // ZONAS
            // =================================================

            locationsMode -> {

                LocationsScreen(

                    onBackClick = {
                        locationsMode = false
                    },

                    onAddLocationClick = {
                        addLocationMode = true
                    },

                    onEditLocationClick = { location ->

                        homeViewModel
                            .loadContactsForLocation(
                                location.id
                            )

                        editLocation = location
                    }
                )
            }


            // =================================================
            // ADMIN
            // =================================================

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
                    },

                    onDevTestsClick = {
                        devTestesMode = true
                    }
                )
            }
        }
    }


    // =========================================================
    // ESTADO DAS PERMISSÕES
    // =========================================================

    private enum class PermissionStep {
        NONE,

        LOCATION,
        LOCATION_DENIED,

        BACKGROUND_LOCATION,
        BACKGROUND_SETTINGS,

        SMS,
        SMS_DENIED,

        NOTIFICATIONS,
        NOTIFICATIONS_DENIED
    }
}