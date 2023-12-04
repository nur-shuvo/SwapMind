package com.developerspace.webrtcsample.compose

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.developerspace.webrtcsample.ChatMainActivity
import com.developerspace.webrtcsample.MainActivity
import com.developerspace.webrtcsample.compose.ui.screens.ActiveUsersScreen
import com.developerspace.webrtcsample.compose.ui.screens.MainScreen
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// This is the entry point of all composes. Single activity to handle all screen.
class ComposeMainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MainScreen()
            }
        }
        makeStatusOnlineToServer()
    }
}

private fun makeStatusOnlineToServer() {
    // Coming to mainActivity indicates user is online now
    val realTimeDb = Firebase.database
    val auth = Firebase.auth
    realTimeDb.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD).child(auth.uid.toString())
        .setValue(User(auth.uid.toString(), getUserName(), getPhotoUrl(), true))
        .addOnFailureListener {
            Log.i("ComposeMainActivity", it.message.toString())
        }
}

private fun getPhotoUrl(): String? {
    val auth = Firebase.auth
    val user = auth.currentUser
    return user?.photoUrl?.toString()
}

private fun getUserName(): String? {
    val auth = Firebase.auth
    val user = auth.currentUser
    return if (user != null) {
        user.displayName
    } else ChatMainActivity.ANONYMOUS
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyTheme {
        MainScreen()
    }
}