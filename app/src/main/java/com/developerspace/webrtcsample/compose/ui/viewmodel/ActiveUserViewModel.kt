package com.developerspace.webrtcsample.compose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerspace.webrtcsample.compose.data.repository.UserListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ActiveUserViewModel @Inject constructor(
    private val userListRepository: UserListRepository
) : ViewModel() {

    // ui states
    val userListState = userListRepository.getUserListFromDbFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = mutableListOf(),
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.i("fetchAllUsersRemote start")
            userListRepository.fetchAllUsersRemote()
        }
    }

    companion object {
        private const val TAG = "ActiveUserViewModel"
    }
}