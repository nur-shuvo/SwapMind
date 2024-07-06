package com.developerspace.webrtcsample.compose.ui.util

import com.developerspace.webrtcsample.compose.data.model.RemoteStory
import com.developerspace.webrtcsample.compose.data.model.STORIES_REMOTE_PATH
import com.developerspace.webrtcsample.compose.data.model.User
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.developerspace.webrtcsample.legacy.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber
import javax.inject.Inject

/**
 * Update current firebase user remote
 */
class UserUpdateRemoteUtil @Inject constructor() {

    fun makeUserOnlineRemote(realTimeDb: FirebaseDatabase, auth: FirebaseAuth) {
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD)
            .child(auth.uid.toString())
            .setValue(User(auth.uid.toString(), getUserName(auth), getPhotoUrl(auth), true))
            .addOnFailureListener {
                Timber.i(it.message.toString())
            }
    }

    fun makeUserOfflineRemote(realTimeDb: FirebaseDatabase, auth: FirebaseAuth) {
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD)
            .child(auth.uid.toString())
            .setValue(User(auth.uid.toString(), getUserName(auth), getPhotoUrl(auth), false))
            .addOnFailureListener {
                Timber.i(it.message.toString())
            }
    }

    fun modifyUserName(realTimeDb: FirebaseDatabase, auth: FirebaseAuth, newName: String) {
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD)
            .child(auth.uid.toString())
            .setValue(User(auth.uid.toString(), newName, getPhotoUrl(auth), false))
            .addOnFailureListener {
                Timber.i(it.message.toString())
            }
    }

    fun modifyUserProfileUrl(
        realTimeDb: FirebaseDatabase,
        auth: FirebaseAuth,
        newProfileUrl: String
    ) {
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD)
            .child(auth.uid.toString())
            .setValue(User(auth.uid.toString(), getUserName(auth), newProfileUrl, false))
            .addOnFailureListener {
                Timber.i(it.message.toString())
            }
    }

    fun updateUserRemoteStory(
        realTimeDb: FirebaseDatabase,
        auth: FirebaseAuth,
        remoteStory: RemoteStory
    ) {
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(STORIES_REMOTE_PATH)
            .child(auth.uid.toString())
            .setValue(remoteStory)
    }

    private fun getPhotoUrl(auth: FirebaseAuth): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(auth: FirebaseAuth): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ChatMainActivity.ANONYMOUS
    }
}