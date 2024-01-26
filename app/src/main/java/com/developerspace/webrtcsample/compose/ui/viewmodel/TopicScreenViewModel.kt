package com.developerspace.webrtcsample.compose.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerspace.webrtcsample.compose.data.repository.UserListRepository
import com.developerspace.webrtcsample.compose.ui.util.Topic
import com.developerspace.webrtcsample.model.User
import com.developerspace.webrtcsample.worker.UpdateTopicOnlineUserWorker
import com.developerspace.webrtcsample.worker.UpdateTopicOnlineUserWorker.Companion.ADD
import com.developerspace.webrtcsample.worker.UpdateTopicOnlineUserWorker.Companion.DELETE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val appContext: Context,
    private val userListRepository: UserListRepository
) : ViewModel() {

    private val _userListState: MutableStateFlow<List<User>> =
        MutableStateFlow(mutableListOf())
    val userListState: StateFlow<List<User>> = _userListState.asStateFlow()

    private var isStartedActiveUserGenerationTask = false
    private val currentUserID = Firebase.auth.uid.toString()
    private val currentPhotoUrl = Firebase.auth.currentUser?.photoUrl?.toString()
    private val currentUserName = Firebase.auth.currentUser?.displayName
    private var currentTopic: Topic? = null

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
        currentTopic = topic
        if (isStartedActiveUserGenerationTask.not()) {
            isStartedActiveUserGenerationTask = true
            viewModelScope.launch(Dispatchers.IO) {
                userListRepository.fetchAllActiveUsersByTopic(topic) {
                    _userListState.value = it.toMutableList()
                }
            }
            // Also request to add currentUserID to active-list on the remote
            UpdateTopicOnlineUserWorker.enqueueWork(
                appContext,
                ADD,
                User(currentUserID, currentUserName, currentPhotoUrl, true),
                topic.topicTitle
            )
        }
    }

    override fun onCleared() {
        // Also request to delete currentUserID to active-list on the remote
        UpdateTopicOnlineUserWorker.enqueueWork(
            appContext,
            DELETE,
            User(currentUserID, currentUserName, currentPhotoUrl, false),
            currentTopic?.topicTitle ?: ""
        )
        super.onCleared()
    }

    companion object {
        const val INTERVAL = 5 * 1000L
    }
}