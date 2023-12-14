package com.developerspace.webrtcsample.compose.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Timer
import java.util.TimerTask

class HomeScreenViewModel : ViewModel() {

    private val _countState = MutableStateFlow(0)
    val countState: StateFlow<Int> = _countState.asStateFlow()

    init {
        val t = Timer()
        t.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val newVal = _countState.value + 1
                _countState.value = newVal
            }
        }, 1000, 2000)
    }
}