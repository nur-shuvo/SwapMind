package com.developerspace.webrtcsample.compose.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerspace.webrtcsample.compose.data.repository.ChatRepository
import com.developerspace.webrtcsample.compose.worker.SignOutWorker
import com.developerspace.webrtcsample.compose.worker.UpsertRecentChatWorker
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

    fun resetUnreadCount(readMessageFromUser: String) {
        viewModelScope.launch {
            chatRepository.resetUnreadCountInDb(readMessageFromUser)
        }
    }

    fun resetUnreadCountUsingWorker(context: Context, readMessageFromUser: String) {
        val bundle = Bundle().apply {
            putString("toUserId", readMessageFromUser)
        }
        UpsertRecentChatWorker.enqueueWork(
            context,
            UpsertRecentChatWorker.RESET_UNREAD_COUNT,
            bundle
        )
    }

    companion object {
        private const val TAG = "ChatListViewModel"
    }
}