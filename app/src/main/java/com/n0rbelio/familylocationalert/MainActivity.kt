package com.n0rbelio.familylocationalert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.n0rbelio.familylocationalert.ui.HomeScreen
import com.n0rbelio.familylocationalert.ui.theme.FamilyLocationAlertTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FamilyLocationAlertTheme {
                HomeScreen()
            }
        }
    }
}