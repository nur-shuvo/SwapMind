package com.developerspace.webrtcsample.compose.ui.viewmodel

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.developerspace.webrtcsample.compose.ui.util.UserUpdateRemoteUtil
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AccountProfileEditViewModel : ViewModel() {

    fun updateCurrentUserProfileName(activity: Activity, newName: String) {
        Firebase.auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(newName).build()
        )?.addOnSuccessListener {
            Log.i(UserDetailViewModel.TAG, "updateCurrentUserProfileName successful")
            Toast.makeText(activity, "Profile name updated!", Toast.LENGTH_LONG).show()

            // update user object in firebase remote
            UserUpdateRemoteUtil().modifyUserName(Firebase.database, Firebase.auth, newName)
        }?.addOnFailureListener {
            Log.e(UserDetailViewModel.TAG, "updateProfile name error")
        }
    }
}