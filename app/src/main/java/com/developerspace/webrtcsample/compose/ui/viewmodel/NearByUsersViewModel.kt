package com.developerspace.webrtcsample.compose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerspace.webrtcsample.compose.data.model.User
import com.developerspace.webrtcsample.compose.data.repository.UserListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NearByUsersViewModel @Inject constructor(
    private val userListRepository: UserListRepository
) : ViewModel() {

    private val _nearByUserListState: MutableStateFlow<List<Pair<User, Long>>> =
        MutableStateFlow(mutableListOf())
    val nearByUserListState = _nearByUserListState.asStateFlow()

    private val _isProgressLoading = MutableStateFlow(false)
    val isProgressLoading: StateFlow<Boolean> = _isProgressLoading.asStateFlow()

    init {
        _isProgressLoading.value = true
        userListRepository.fetchAllNearByUsers(NEARBY_USER_DIS) {
            if (_isProgressLoading.value) {
                _isProgressLoading.value = false
                _nearByUserListState.value = it
                it.forEach { item ->
                    Timber.i("nearby uid ${item.first.userID} distance in Km ${item.second}")
                }
            }
        }
        stopProgressIfTimeout()
    }

    private fun stopProgressIfTimeout() {
        viewModelScope.launch {
            delay(PROGRESS_TIMEOUT)
            _isProgressLoading.value = false
        }
    }

    companion object {
        const val NEARBY_USER_DIS = 500L
        const val PROGRESS_TIMEOUT = 10*1000L
    }
}