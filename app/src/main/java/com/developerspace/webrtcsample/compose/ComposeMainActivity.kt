package com.developerspace.webrtcsample.compose

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.developerspace.webrtcsample.compose.ui.navigation.TopLevelNavigation
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.util.REQUEST_LOCATION_PERMISSION
import com.developerspace.webrtcsample.compose.ui.util.UserUpdateRemoteUtil
import com.developerspace.webrtcsample.compose.ui.util.tryToSetUserLocation
import com.developerspace.webrtcsample.compose.fcm.UserFcmTokenUpdateUtil
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.developerspace.webrtcsample.compose.data.preference.AppPref
import com.developerspace.webrtcsample.compose.ui.util.REQUEST_GPS
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

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
        tryToSetUserLocation()
    }

    override fun onDestroy() {
        // user is offline now
        UserUpdateRemoteUtil().makeUserOfflineRemote(Firebase.database, Firebase.auth)
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun updateFcmTokenToRemote() {
        appPref.getFcmDeviceToken().run {
            Timber.i("token in local-$this")
            if (isNotEmpty()) {
                UserFcmTokenUpdateUtil().updateFcmDeviceTokenToRemote(
                    Firebase.database,
                    Firebase.auth,
                    this
                )
            } else {
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    Timber.i("token from firebase-api call-$it")
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
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.i("Location Permission granted")
                tryToSetUserLocation()
            } else {
                Timber.i("Location Permission denied")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GPS) {
            tryToSetUserLocation()
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