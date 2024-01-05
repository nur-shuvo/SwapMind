package com.developerspace.webrtcsample.compose.ui.viewmodel

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.developerspace.webrtcsample.compose.repository.RemoteStoriesRepository
import com.developerspace.webrtcsample.compose.ui.util.UserUpdateRemoteUtil
import com.developerspace.webrtcsample.model.RemoteStory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val remoteStoriesRepository: RemoteStoriesRepository
) : ViewModel() {

    // ui state
    private val _remoteStoryList = MutableStateFlow<MutableList<RemoteStory>>(mutableListOf())
    val remoteStoryList: StateFlow<List<RemoteStory>> = _remoteStoryList.asStateFlow()

    init {
        remoteStoriesRepository.fetchAllStoriesRemote {
            _remoteStoryList.value = it.toMutableList()
        }
    }

    fun onAddStoryImageSelected(activity: Activity, uri: Uri) {
        val storageReference = Firebase.storage
            .getReference(Firebase.auth.uid!!)
            .child("story_pic")
        putImageInStorage(activity, storageReference, uri)
    }

    private fun putImageInStorage(
        activity: Activity,
        storageReference: StorageReference,
        uri: Uri
    ) {
        // First upload the image to Cloud Storage
        storageReference.putFile(uri)
            .addOnSuccessListener(activity) { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
                // and add it to the message.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        updateCurrentUserRemoteStory(uri)
                    }
            }
            .addOnFailureListener(activity) { e ->
                Log.e(AccountProfileViewModel.TAG, "Image upload task was unsuccessful.", e)
            }
    }

    private fun updateCurrentUserRemoteStory(uri: Uri) {
        val auth = Firebase.auth
        UserUpdateRemoteUtil().updateUserRemoteStory(
            Firebase.database,
            auth,
            RemoteStory(auth.uid, uri.toString(), System.currentTimeMillis().toString(), "")
        )
    }
}