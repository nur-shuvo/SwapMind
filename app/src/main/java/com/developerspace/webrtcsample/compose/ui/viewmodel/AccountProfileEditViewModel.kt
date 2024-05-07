package com.developerspace.webrtcsample.compose.ui.viewmodel

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerspace.webrtcsample.compose.ui.util.UserUpdateRemoteUtil
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class AccountProfileEditViewModel : ViewModel() {

    private val _isProgressLoading = MutableStateFlow(false)
    val isProgressLoading: StateFlow<Boolean> = _isProgressLoading.asStateFlow()
    fun updateCurrentUserProfileName(activity: Activity, newName: String, onCompleted: () -> Unit) {
        _isProgressLoading.value = true
        Firebase.auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(newName).build()
        )?.addOnSuccessListener {
            Timber.i("updateCurrentUserProfileName successful")
            Toast.makeText(activity, "Profile name updated!", Toast.LENGTH_LONG).show()
            // update user object in firebase remote
            UserUpdateRemoteUtil().modifyUserName(Firebase.database, Firebase.auth, newName)
            viewModelScope.launch {
                delay(2000L)
                _isProgressLoading.value = false
                onCompleted.invoke()
            }
        }?.addOnFailureListener {
            Timber.e("updateProfile name error")
            onCompleted.invoke()
        }
    }
}