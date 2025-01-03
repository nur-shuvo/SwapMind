package com.developerspace.webrtcsample.legacy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.legacy.activeUsers.ui.ActiveUserActivity
import com.developerspace.webrtcsample.compose.ui.util.UserUpdateRemoteUtil
import com.firebase.ui.auth.AuthUI
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore
    private val realTimeDb = Firebase.database
    private val auth = Firebase.auth

    private var start_meeting: MaterialButton?= null
    private var meeting_id: EditText?= null
    private var join_meeting: MaterialButton?= null
    private var only_chat: Button?= null
    private var active_users: Button?= null
    private var sign_out: Button?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        start_meeting = findViewById(R.id.start_meeting)
        meeting_id = findViewById(R.id.meeting_id)
        join_meeting = findViewById(R.id.join_meeting)
        only_chat = findViewById(R.id.only_chat)
        active_users = findViewById(R.id.active_users)
        sign_out = findViewById(R.id.sign_out)

        Constants.isIntiatedNow = true
        Constants.isCallEnded = true

        start_meeting?.setOnClickListener {
            if (meeting_id?.text.toString().trim().isNullOrEmpty())
                meeting_id?.error = "Please enter meeting id"
            else {
                db.collection("calls")
                    .document(meeting_id?.text.toString())
                    .get()
                    .addOnSuccessListener {
                        if (it["type"] == "OFFER" || it["type"] == "ANSWER" || it["type"] == "END_CALL") {
                            meeting_id?.error = "Please enter new meeting ID"
                        } else {
//                            val intent = Intent(this@MainActivity, RTCActivity::class.java)
//                            intent.putExtra("meetingID", meeting_id?.text.toString())
//                            intent.putExtra("isJoin", false)
//                            startActivity(intent)
                        }
                    }
                    .addOnFailureListener {
                        meeting_id?.error = "Please enter new meeting ID"
                    }
            }
        }
        join_meeting?.setOnClickListener {
            if (meeting_id?.text.toString().trim().isNullOrEmpty())
                meeting_id?.error = "Please enter meeting id"
            else {
//                val intent = Intent(this@MainActivity, RTCActivity::class.java)
//                intent.putExtra("meetingID", meeting_id?.text.toString())
//                intent.putExtra("isJoin", true)
//                startActivity(intent)
            }
        }
        only_chat?.setOnClickListener {
            // Need to think the flow
//            val intent = Intent(this@MainActivity, ChatMainActivity::class.java)
//            startActivity(intent)
        }

        active_users?.setOnClickListener {
            val intent = Intent(this@MainActivity, ActiveUserActivity::class.java)
            startActivity(intent)
        }

        sign_out?.setOnClickListener {
            signOut()
        }

        // Coming to mainActivity indicates user is online now
        UserUpdateRemoteUtil().makeUserOnlineRemote(Firebase.database, Firebase.auth)
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ChatMainActivity.ANONYMOUS
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener {
            gotoSignInActivity()
        }
    }

    private fun gotoSignInActivity() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        // user is offline now
        UserUpdateRemoteUtil().makeUserOfflineRemote(Firebase.database, Firebase.auth)
        super.onDestroy()
    }
    companion object {
        const val ONLINE_USER_LIST_CHILD = "onlineuserlist"
    }
}