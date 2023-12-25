package com.developerspace.webrtcsample.compose.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.developerspace.webrtcsample.legacy.MainActivity
import com.developerspace.webrtcsample.legacy.SignInActivity
import com.developerspace.webrtcsample.compose.ui.util.AppLevelCache
import com.developerspace.webrtcsample.compose.ui.util.UserUpdateRemoteUtil
import com.developerspace.webrtcsample.model.User
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountProfileViewModel : ViewModel() {

    // ui state
    private val _userProfileState = MutableStateFlow(User())
    val userProfileState: StateFlow<User> = _userProfileState.asStateFlow()

    init {
        Firebase.database.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD)
            .child(Firebase.auth.uid.toString()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue<User>()?.let {
                        val newUser = User(it.userID, it.userName, it.photoUrl, it.onlineStatus)
                        _userProfileState.value = newUser
                        AppLevelCache.userProfiles?.set(AppLevelCache.currentUserItemKey, newUser)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // TODO("Not yet implemented")
                }
            })
    }

    fun setUserProfile(user: User) {
        _userProfileState.value = user
        AppLevelCache.userProfiles?.set(AppLevelCache.currentUserItemKey, user)
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
            AppLevelCache.userProfiles?.set(AppLevelCache.currentUserItemKey, newUser)

            // update photo url in user remote
            UserUpdateRemoteUtil().modifyUserProfileUrl(Firebase.database, Firebase.auth, uri.toString())
        }?.addOnFailureListener {
            Log.e(TAG, "updateProfile image error")
        }
    }

    fun signOut(context: Context) {
        AuthUI.getInstance().signOut(context).addOnSuccessListener {
            gotoSignInActivity(context)
        }
    }

    private fun gotoSignInActivity(context: Context) {
        context.startActivity(Intent(context, SignInActivity::class.java))
        // finishing compose activity
        (context as AppCompatActivity).finish()
    }

    companion object {
        const val TAG = "UserDetailViewModel"
    }
}