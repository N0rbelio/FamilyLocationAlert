package com.example.familylocationalert.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    var monitoring by mutableStateOf(false)
        private set

    fun startMonitoring() {
        monitoring = true
    }

    fun stopMonitoring() {
        monitoring = false
    }
}