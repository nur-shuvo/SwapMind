package com.developerspace.webrtcsample.compose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveStreamViewModel @Inject constructor(
) : ViewModel() {

    private val _streams = MutableStateFlow<List<Stream>>(emptyList())
    val streams: StateFlow<List<Stream>> = _streams

    init {
        fetchStreams()
    }

    private fun fetchStreams() {
        viewModelScope.launch {
            val response =
                listOf(
                    Stream("Shuvo", "first_channel", ""),
                    Stream("Wakil", "second_channel", "")
                )
            _streams.value = response
        }
    }
}

data class Stream(
    val broadcasterName: String,
    val channelId: String,
    val profileImageUrl: String
)
