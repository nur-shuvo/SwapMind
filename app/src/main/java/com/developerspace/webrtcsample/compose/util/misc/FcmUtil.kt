package com.developerspace.webrtcsample.compose.util.misc

import android.content.Context
import android.util.Log
import com.developerspace.webrtcsample.compose.fcm.FcmV1ApiToken
import com.developerspace.webrtcsample.compose.fcm.UserFcmTokenUpdateUtil.Companion.DEVICE_FCM_TOKEN_PATH
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.developerspace.webrtcsample.compose.network.RestApiService
import com.developerspace.webrtcsample.compose.network.requestVO.FcmMessageRequestBody
import com.developerspace.webrtcsample.compose.network.requestVO.Message
import com.developerspace.webrtcsample.compose.network.requestVO.Notification
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class FcmUtil @Inject constructor(
    private val apiService: RestApiService,
    @ApplicationContext private val context: Context
) {
    companion object {
        const val TAG = "FcmUtil"
        const val SWAP_MIND_FIREBASE_PROJECT_ID = "talkbuddy-33d19"
    }

    private val workerScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun sendPushNotificationToReceiver(
        receiverUserID: String,
        receiverUserName: String,
        messageText: String = "",
        isImageType: Boolean = false
    ) {
        workerScope.launch(Dispatchers.IO) {
            val token = FcmV1ApiToken.getAccessToken(context)
            Log.i(TAG, "access-token $token")

            // get receiverUserId Device-token
            Firebase.database.reference.child(ChatMainActivity.ROOT).child(DEVICE_FCM_TOKEN_PATH)
                .child(receiverUserID).get()
                .addOnSuccessListener { snapShot ->
                    snapShot.getValue<String>()?.let { receiverDeviceToken ->
                        Log.i(TAG, "Received device-token $receiverDeviceToken")
                        workerScope.launch(Dispatchers.IO) {
                            // create headers
                            val headerMap: MutableMap<String, String> = mutableMapOf()
                            headerMap["Content-Type"] = "application/json"
                            headerMap["Authorization"] = "Bearer $token"

                            // create request body
                            val reqBody =
                                FcmMessageRequestBody(
                                    message = Message(
                                        token = receiverDeviceToken, notification = Notification(
                                            body = if (isImageType) "Sent a photo" else messageText,
                                            title = receiverUserName
                                        )
                                    )
                                )

                            apiService.sendFcmMessageByDeviceToken(
                                "https://fcm.googleapis.com/v1/projects/$SWAP_MIND_FIREBASE_PROJECT_ID/messages:send",
                                headerMap,
                                reqBody
                            )
                        }
                    }
                }
        }
    }
}