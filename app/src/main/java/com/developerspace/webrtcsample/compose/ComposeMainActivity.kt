package com.developerspace.webrtcsample.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.screens.MainScreen

// This is the entry point of all composes. Single activity to handle all screen.
class ComposeMainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyTheme {
        MainScreen()
    }
}