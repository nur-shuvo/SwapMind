package com.developerspace.webrtcsample.compose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerspace.webrtcsample.compose.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {

    // ui states
    val recentMessageListState = chatRepository.getChatListFromDbFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = mutableListOf(),
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i(TAG, "fetchChatListRemote start")
            chatRepository.fetchChatListRemote()
        }
    }

    companion object {
        private const val TAG = "ChatListViewModel"
    }
}