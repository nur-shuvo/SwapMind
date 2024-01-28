package com.developerspace.webrtcsample.compose.fcm

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.auth.oauth2.GoogleCredentials
import java.io.IOException
import java.io.InputStream

/**
 * Fetch access token for google api access.
 * Ref: https://firebase.google.com/docs/cloud-messaging/migrate-v1
 */
class FcmV1ApiToken {
    companion object {
        private const val SCOPES = "https://www.googleapis.com/auth/firebase.messaging"
        private const val TAG = "FcmV1ApiToken"

        @Throws(IOException::class)
        @WorkerThread
        fun getAccessToken(context: Context): String? {
            var inputStream: InputStream? = null
            try {
                val assetManager = context.assets
                inputStream = assetManager.open("service-account.json")

                val googleCredentials: GoogleCredentials = GoogleCredentials
                    .fromStream(inputStream)
                    .createScoped(listOf(SCOPES))
                googleCredentials.refresh()
                return googleCredentials.accessToken.tokenValue
            } catch (e: Exception) {
                Log.i(TAG, "exception in getAccessToken $e")
                inputStream?.close()
            }
            return null
        }
    }
}