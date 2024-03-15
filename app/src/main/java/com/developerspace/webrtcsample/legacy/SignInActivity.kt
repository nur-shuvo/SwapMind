package com.developerspace.webrtcsample.legacy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.ComposeMainActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SignInActivity"
    }
    private lateinit var auth: FirebaseAuth
    private val signIn: ActivityResultLauncher<Intent> =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "Sign in successful!")
                goToMainActivity()
            } else {
                Toast.makeText(
                    this,
                    "There was an error signing in",
                    Toast.LENGTH_LONG).show()

                val response = result.idpResponse
                if (response == null) {
                    Log.w(TAG, "Sign in canceled")
                } else {
                    Log.w(TAG, "Sign in error", response.error)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser == null) {
            // Sign in with FirebaseUI, see docs for more details:
            // https://firebase.google.com/docs/auth/android/firebaseui
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.swap_mind_new_logo)
                .setAvailableProviders(listOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                ))
                .build()

            signIn.launch(signInIntent)
        } else {
            goToMainActivity()
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, ComposeMainActivity::class.java))
    }
}