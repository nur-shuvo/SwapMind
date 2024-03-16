package com.developerspace.webrtcsample.legacy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.data.model.FriendlyMessage
import com.developerspace.webrtcsample.compose.util.misc.FcmUtil
import com.developerspace.webrtcsample.compose.util.misc.MyOpenDocumentContract
import com.developerspace.webrtcsample.compose.worker.UpsertRecentChatWorker
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChatMainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: FriendlyMessageAdapter
    private var receiverUserID = ""
    private var receiverUserName = ""
    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        uri?.let { onImageSelected(it) }
    }

    private var progressBar: ProgressBar? = null
    private var messageRecyclerView: RecyclerView? = null
    private var sendButton: ImageView? = null
    private var messageEditText: EditText? = null
    private var addMessageImageView: ImageView? = null
    private var toolbar: androidx.appcompat.widget.Toolbar? = null

    @Inject
    lateinit var fcmUtil: FcmUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        progressBar = findViewById(R.id.progressBar)
        messageRecyclerView = findViewById(R.id.messageRecyclerView)
        sendButton = findViewById(R.id.sendButton)
        messageEditText = findViewById(R.id.messageEditText)
        addMessageImageView = findViewById(R.id.addMessageImageView)
        toolbar = findViewById(R.id.my_custom_toolbar)

        auth = Firebase.auth
        db = Firebase.database
        receiverUserID = intent.getStringExtra("receiverUserID").toString()
        receiverUserName = intent.getStringExtra("receiverUserName").toString()

        toolbar?.title = receiverUserName
        setSupportActionBar(toolbar)

        val messagesRef = db.reference.child(ROOT).child(MESSAGES_CHILD)
            .child(getUniqueIDForMessage(auth.uid.toString(), receiverUserID))

        // The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
        // See: https://github.com/firebase/FirebaseUI-Android
        val options = FirebaseRecyclerOptions.Builder<FriendlyMessage>()
            .setQuery(messagesRef, FriendlyMessage::class.java)
            .build()
        adapter = FriendlyMessageAdapter(options, getUserName())
        progressBar?.visibility = ProgressBar.INVISIBLE
        manager = WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        manager.stackFromEnd = true
        messageRecyclerView?.layoutManager = manager
        messageRecyclerView?.adapter = adapter

        // Scroll down when a new message arrives, See MyScrollToBottomObserver for details
        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(messageRecyclerView!!, adapter, manager)
        )

        sendButton?.setOnClickListener {
            if (messageEditText?.text.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    "Please enter something!",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val friendlyMessage = FriendlyMessage(
                messageEditText!!.text.toString(),
                getUserName(),
                getPhotoUrl(),
                null,
                time = System.currentTimeMillis().toString()
            )

            // message
            db.reference.child(ROOT).child(MESSAGES_CHILD)
                .child(getUniqueIDForMessage(auth.uid.toString(), receiverUserID))
                .push()
                .setValue(friendlyMessage)

            // recent messages
            db.reference.child(ROOT).child(MESSAGES_CHILD).child(RECENT_MESSAGES_CHILD)
                .child(auth.uid.toString())
                .child(receiverUserID)
                .setValue(friendlyMessage)
            db.reference.child(ROOT).child(MESSAGES_CHILD).child(RECENT_MESSAGES_CHILD)
                .child(receiverUserID)
                .child(auth.uid.toString())
                .setValue(friendlyMessage)

            messageEditText?.setText("")

            // notification send to device
            fcmUtil.sendPushNotificationToReceiver(
                receiverUserID,
                getUserName().toString(),
                friendlyMessage.text.toString()
            )
        }

        addMessageImageView?.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }
    }

    private fun getUniqueIDForMessage(x: String, y: String): String {
        if (x < y) return "$x:$y"
        return "$y:$x"
    }

    public override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onDestroy() {
        resetUnreadCountUsingWorker(this, receiverUserID)
        super.onDestroy()
    }

    private fun resetUnreadCountUsingWorker(context: Context, readMessageFromUser: String) {
        val bundle = Bundle().apply {
            putString("toUserId", readMessageFromUser)
        }
        UpsertRecentChatWorker.enqueueWork(
            context,
            UpsertRecentChatWorker.RESET_UNREAD_COUNT,
            bundle
        )
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

    private fun onImageSelected(uri: Uri) {
        Timber.tag(TAG).d("Uri: %s", uri)
        val user = auth.currentUser
        val tempMessage = FriendlyMessage(null, getUserName(), getPhotoUrl(), LOADING_IMAGE_URL)

        db.reference.child(ROOT).child(MESSAGES_CHILD)
            .child(getUniqueIDForMessage(auth.uid.toString(), receiverUserID))
            .push()
            .setValue(
                tempMessage,
                DatabaseReference.CompletionListener { databaseError, databaseReference ->
                    if (databaseError != null) {
                        Timber.tag(TAG)
                            .w(databaseError.toException(), "Unable to write message to database.")
                        return@CompletionListener
                    }

                    // Build a StorageReference and then upload the file
                    val key = databaseReference.key
                    val storageReference = Firebase.storage
                        .getReference(user!!.uid)
                        .child(key!!)
                        .child(uri.lastPathSegment!!)
                    putImageInStorage(storageReference, uri, key)
                })
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        // First upload the image to Cloud Storage
        storageReference.putFile(uri)
            .addOnSuccessListener(
                this
            ) { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
                // and add it to the message.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val friendlyMessage =
                            FriendlyMessage(null, getUserName(), getPhotoUrl(), uri.toString())
                        db.reference.child(ROOT).child(MESSAGES_CHILD)
                            .child(getUniqueIDForMessage(auth.uid.toString(), receiverUserID))
                            .child(key!!)
                            .setValue(friendlyMessage)
                    }
            }
            .addOnFailureListener(this) { e ->
                Timber.tag(TAG).w(e, "Image upload task was unsuccessful.")
            }
    }


    // Created for known recyclerview issue:
    // https://stackoverflow.com/questions/31759171/recyclerview-and-java-lang-indexoutofboundsexception-inconsistency-detected-in
    class WrapContentLinearLayoutManager(
        context: Context,
        @RecyclerView.Orientation orientation: Int,
        reverseLayout: Boolean
    ) : LinearLayoutManager(context, orientation, reverseLayout) {
        override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Timber.tag("TAG").e("meet a IOOBE in RecyclerView")
            }
        }
    }

    companion object {
        private const val TAG = "ChatMainActivity"
        const val MESSAGES_CHILD = "messages"
        const val RECENT_MESSAGES_CHILD = "recentmessages"
        const val ROOT = "root"
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }
}