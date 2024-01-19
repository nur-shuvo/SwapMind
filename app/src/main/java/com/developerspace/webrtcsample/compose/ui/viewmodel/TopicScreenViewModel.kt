package com.developerspace.webrtcsample.compose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerspace.webrtcsample.compose.repository.UserListRepository
import com.developerspace.webrtcsample.compose.ui.util.Topic
import com.developerspace.webrtcsample.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TopicScreenViewModel @Inject constructor(
    private val userListRepository: UserListRepository
) : ViewModel() {

    private val _userListState: MutableStateFlow<List<User>> =
        MutableStateFlow(mutableListOf())
    val userListState: StateFlow<List<User>> = _userListState.asStateFlow()

    private var isStartedActiveUserGenerationTask = false

    init {
        // Shuffle the list at every 5 seconds
        val periodicShuffleFlow: Flow<List<User>> = flow {
            while (true) {
                val currentList = _userListState.value
                val ret = currentList.toMutableList()
                ret.shuffle()
                emit(ret)
                delay(INTERVAL)
            }
        }.flowOn(Dispatchers.IO)

        viewModelScope.launch(Dispatchers.Main) {
            periodicShuffleFlow.collect { rcvdList ->
                if (rcvdList.isEmpty()) return@collect
                Timber.i("${rcvdList[0].userName}")
                _userListState.value = rcvdList
            }
        }
    }

    /**
     * Start updating topic active user list, after it is called, it will update automatically
     * if data at remote changes
     */
    fun startGeneratingActiveUserIfNeeded(topic: Topic) {
        if (isStartedActiveUserGenerationTask.not()) {
            isStartedActiveUserGenerationTask = true
            viewModelScope.launch(Dispatchers.IO) {
                userListRepository.fetchAllActiveUsersByTopic(topic) {
                    _userListState.value = it.toMutableList()
                }
            }
        }
    }

    companion object {
        const val INTERVAL = 5*1000L
    }
}