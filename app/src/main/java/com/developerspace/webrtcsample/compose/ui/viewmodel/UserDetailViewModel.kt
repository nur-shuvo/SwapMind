package com.developerspace.webrtcsample.compose.ui.viewmodel

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.developerspace.webrtcsample.model.User
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserDetailViewModel : ViewModel() {

    // ui state
    private val _userProfileState = MutableStateFlow(User())
    val userProfileState: StateFlow<User> = _userProfileState.asStateFlow()

    fun setUserProfile(user: User) {
        _userProfileState.value = user
    }

    fun onProfileImageEditSelected(activity: Activity, uri: Uri) {
        val storageReference = Firebase.storage
            .getReference(Firebase.auth.uid!!)
            .child("profile_pic")
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
                        updateCurrentUserProfileImage(activity, uri)
                    }
            }
            .addOnFailureListener(activity) { e ->
                Log.e(TAG, "Image upload task was unsuccessful.", e)
            }
    }

    private fun updateCurrentUserProfileImage(activity: Activity, uri: Uri) {
        Firebase.auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder().setPhotoUri(uri).build()
        )?.addOnSuccessListener {
            Log.i(TAG, "updateProfile image successful")
            Toast.makeText(activity, "Profile image updated!", Toast.LENGTH_LONG).show()

            val newUser = User(_userProfileState.value.userID, _userProfileState.value.userName,
                uri.toString(), _userProfileState.value.onlineStatus)
            _userProfileState.value = newUser
        }?.addOnFailureListener {
            Log.e(TAG, "updateProfile image error")
        }
    }

    companion object {
        const val TAG = "UserDetailViewModel"
    }
}