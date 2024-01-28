package com.developerspace.webrtcsample.compose.fcm

import android.util.Log
import com.developerspace.webrtcsample.compose.data.preference.AppPref
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var appPref: AppPref
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.i(TAG, "onNewToken arrived $token")
        // Store token in SharedPreference
        appPref.setFcmDeviceToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.i(TAG, "onMessageReceived arrived ${message.from}")
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingService"
    }
}