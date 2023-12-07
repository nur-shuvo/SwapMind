package com.developerspace.webrtcsample.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.developerspace.webrtcsample.compose.navigation.TopLevelNavigation
import com.developerspace.webrtcsample.compose.ui.screens.ActiveUsersScreen
import com.developerspace.webrtcsample.compose.ui.screens.MainScreen
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.util.UserUpdateRemoteUtil
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// This is the entry point of all composes. Single activity to handle all screen/destinations.
class ComposeMainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                TopLevelNavigation()
            }
        }
        UserUpdateRemoteUtil().makeUserOnlineRemote(Firebase.database, Firebase.auth)
    }

    override fun onDestroy() {
        // user is offline now
        UserUpdateRemoteUtil().makeUserOfflineRemote(Firebase.database, Firebase.auth)
        super.onDestroy()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyTheme {
        // MainScreen()
    }
}