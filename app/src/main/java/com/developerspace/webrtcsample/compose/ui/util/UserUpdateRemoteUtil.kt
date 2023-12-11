package com.developerspace.webrtcsample.compose.ui.util

import android.util.Log
import com.developerspace.webrtcsample.ChatMainActivity
import com.developerspace.webrtcsample.MainActivity
import com.developerspace.webrtcsample.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Update current user active/inactive status to server
 */
class UserUpdateRemoteUtil {

    fun makeUserOnlineRemote(realTimeDb: FirebaseDatabase, auth: FirebaseAuth) {
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD).child(auth.uid.toString())
            .setValue(User(auth.uid.toString(), getUserName(auth), getPhotoUrl(auth), true))
            .addOnFailureListener {
                Log.i("MainActivity", it.message.toString())
            }
    }

    fun makeUserOfflineRemote(realTimeDb: FirebaseDatabase, auth: FirebaseAuth) {
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD).child(auth.uid.toString())
            .setValue(User(auth.uid.toString(), getUserName(auth), getPhotoUrl(auth), false))
            .addOnFailureListener {
                Log.i("MainActivity", it.message.toString())
            }
    }

    fun modifyUserName(realTimeDb: FirebaseDatabase, auth: FirebaseAuth, newName: String) {
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD).child(auth.uid.toString())
            .setValue(User(auth.uid.toString(), newName, getPhotoUrl(auth), false))
            .addOnFailureListener {
                Log.i("MainActivity", it.message.toString())
            }
    }

    fun modifyUserProfileUrl(realTimeDb: FirebaseDatabase, auth: FirebaseAuth, newProfileUrl: String) {
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD).child(auth.uid.toString())
            .setValue(User(auth.uid.toString(), getUserName(auth), newProfileUrl, false))
            .addOnFailureListener {
                Log.i("MainActivity", it.message.toString())
            }
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