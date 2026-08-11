package com.example.familylocationalert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.familylocationalert.ui.HomeScreen
import com.example.familylocationalert.ui.theme.FamilyLocationAlertTheme

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