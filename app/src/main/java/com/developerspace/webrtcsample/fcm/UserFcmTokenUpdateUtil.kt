package com.developerspace.webrtcsample.fcm

import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

/**
 * Request for update token of a device in Firebase realtimeDB
 */
class UserFcmTokenUpdateUtil @Inject constructor() {
    companion object {
        const val DEVICE_FCM_TOKEN_PATH = "device-fcm-token"
    }
    fun updateFcmDeviceTokenToRemote(
        realTimeDb: FirebaseDatabase,
        auth: FirebaseAuth,
        fcmDeviceToken: String
    ) {
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(DEVICE_FCM_TOKEN_PATH)
            .child(auth.uid.toString())
            .setValue(fcmDeviceToken)
    }
}