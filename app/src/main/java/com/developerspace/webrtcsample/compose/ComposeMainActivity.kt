package com.developerspace.webrtcsample.compose

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.developerspace.webrtcsample.compose.navigation.TopLevelNavigation
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.util.UserUpdateRemoteUtil
import com.developerspace.webrtcsample.fcm.UserFcmTokenUpdateUtil
import com.developerspace.webrtcsample.preference.AppPref
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// This is the entry point of all composes. Single activity to handle all screen/destinations.
@AndroidEntryPoint
class ComposeMainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ComposeMainActivity"
    }

    @Inject
    lateinit var appPref: AppPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                TopLevelNavigation()
            }
        }
        UserUpdateRemoteUtil().makeUserOnlineRemote(Firebase.database, Firebase.auth)
        askNotificationPermission()
        updateFcmTokenToRemote()
    }

    override fun onDestroy() {
        // user is offline now
        UserUpdateRemoteUtil().makeUserOfflineRemote(Firebase.database, Firebase.auth)
        super.onDestroy()
    }

    private fun updateFcmTokenToRemote() {
        appPref.getFcmDeviceToken().run {
            Log.i(TAG, "token in local-$this")
            if (isNotEmpty()) {
                UserFcmTokenUpdateUtil().updateFcmDeviceTokenToRemote(
                    Firebase.database,
                    Firebase.auth,
                    this
                )
            } else {
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    Log.i("Shuvo", "token from firebase-api call-$it")
                    UserFcmTokenUpdateUtil().updateFcmDeviceTokenToRemote(
                        Firebase.database,
                        Firebase.auth,
                        it
                    )
                    appPref.setFcmDeviceToken(it)
                }
            }
        }
    }

    // permission
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyTheme {
        // MainScreen()
    }
}