package com.developerspace.webrtcsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerspace.webrtcsample.model.FriendlyMessage
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat_main.*

class ChatMainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: FriendlyMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)
        auth = Firebase.auth
        db = Firebase.database

        val messagesRef = db.reference.child(MESSAGES_CHILD)

        // The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
        // See: https://github.com/firebase/FirebaseUI-Android
        val options = FirebaseRecyclerOptions.Builder<FriendlyMessage>()
            .setQuery(messagesRef, FriendlyMessage::class.java)
            .build()
        adapter = FriendlyMessageAdapter(options, getUserName())
        progressBar.visibility = ProgressBar.INVISIBLE
        manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        messageRecyclerView.layoutManager = manager
        messageRecyclerView.adapter = adapter

        // Scroll down when a new message arrives
        // See MyScrollToBottomObserver for details
        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(messageRecyclerView, adapter, manager)
        )

       sendButton.setOnClickListener {
            val friendlyMessage = FriendlyMessage(
                messageEditText.text.toString(),
                getUserName(),
                getPhotoUrl(),
                null
            )
            db.reference.child(MESSAGES_CHILD).push().setValue(friendlyMessage)
            messageEditText.setText("")
        }
    }

    public override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ANONYMOUS
    }

    companion object {
        private const val TAG = "ChatMainActivity"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }
}