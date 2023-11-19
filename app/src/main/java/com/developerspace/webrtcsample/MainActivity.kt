package com.developerspace.webrtcsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.developerspace.webrtcsample.activeUsers.ui.ActiveUserActivity
import com.developerspace.webrtcsample.model.User
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_start.*

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore
    private val realTimeDb = Firebase.database
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Constants.isIntiatedNow = true
        Constants.isCallEnded = true

        start_meeting.setOnClickListener {
            if (meeting_id.text.toString().trim().isNullOrEmpty())
                meeting_id.error = "Please enter meeting id"
            else {
                db.collection("calls")
                    .document(meeting_id.text.toString())
                    .get()
                    .addOnSuccessListener {
                        if (it["type"] == "OFFER" || it["type"] == "ANSWER" || it["type"] == "END_CALL") {
                            meeting_id.error = "Please enter new meeting ID"
                        } else {
                            val intent = Intent(this@MainActivity, RTCActivity::class.java)
                            intent.putExtra("meetingID", meeting_id.text.toString())
                            intent.putExtra("isJoin", false)
                            startActivity(intent)
                        }
                    }
                    .addOnFailureListener {
                        meeting_id.error = "Please enter new meeting ID"
                    }
            }
        }
        join_meeting.setOnClickListener {
            if (meeting_id.text.toString().trim().isNullOrEmpty())
                meeting_id.error = "Please enter meeting id"
            else {
                val intent = Intent(this@MainActivity, RTCActivity::class.java)
                intent.putExtra("meetingID", meeting_id.text.toString())
                intent.putExtra("isJoin", true)
                startActivity(intent)
            }
        }
        only_chat.setOnClickListener {
            // Need to think the flow
//            val intent = Intent(this@MainActivity, ChatMainActivity::class.java)
//            startActivity(intent)
        }

        active_users.setOnClickListener {
            val intent = Intent(this@MainActivity, ActiveUserActivity::class.java)
            startActivity(intent)
        }

        sign_out.setOnClickListener {
            signOut()
        }

        // Coming to mainActivity indicates user is online now
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(ONLINE_USER_LIST_CHILD).child(auth.uid.toString())
            .setValue(User(auth.uid.toString(), getUserName(), getPhotoUrl(), true))
            .addOnFailureListener {
                Log.i("MainActivity", it.message.toString())
            }
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
            finish()
        }
    }

    private fun gotoSignInActivity() {
        startActivity(Intent(this, SignInActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        // user is offline now
        realTimeDb.reference.child(ChatMainActivity.ROOT).child(ONLINE_USER_LIST_CHILD).child(auth.uid.toString())
            .setValue(User(auth.uid.toString(), auth.currentUser?.displayName, getPhotoUrl(), false))
            .addOnFailureListener {
                Log.i("MainActivity", it.message.toString())
            }
    }
    companion object {
        const val ONLINE_USER_LIST_CHILD = "onlineuserlist"
    }
}