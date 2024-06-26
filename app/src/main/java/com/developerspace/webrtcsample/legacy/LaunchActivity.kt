package com.developerspace.webrtcsample.legacy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.developerspace.webrtcsample.compose.ComposeMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        if (auth.currentUser == null) {
            // Not signed in, launch the Sign In activity
            gotoSignInActivity()
            finish()
            return
        } else {
            goToMainActivity()
            finish()
            return
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }
    }

    private fun gotoSignInActivity() {
        startActivity(Intent(this, SignInActivity::class.java))
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, ComposeMainActivity::class.java))
    }
}