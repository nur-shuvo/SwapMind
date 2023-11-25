package com.developerspace.webrtcsample

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.developerspace.webrtcsample.model.FriendlyMessage
import com.developerspace.webrtcsample.util.MyOpenDocumentContract
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_chat_main.*

class ChatMainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: FriendlyMessageAdapter
    private var receiverUserID = ""
    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        uri?.let { onImageSelected(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)
        auth = Firebase.auth
        db = Firebase.database
        receiverUserID = intent.getStringExtra("receiverUserID").toString()

        val messagesRef = db.reference.child(ROOT).child(MESSAGES_CHILD).child(getSmallerUserID(auth.uid.toString(), receiverUserID))

        // The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
        // See: https://github.com/firebase/FirebaseUI-Android
        val options = FirebaseRecyclerOptions.Builder<FriendlyMessage>()
            .setQuery(messagesRef, FriendlyMessage::class.java)
            .build()
        adapter = FriendlyMessageAdapter(options, getUserName())
        progressBar.visibility = ProgressBar.INVISIBLE
        manager = WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        manager.stackFromEnd = true
        messageRecyclerView.layoutManager = manager
         messageRecyclerView.adapter = adapter

//         Scroll down when a new message arrives
//         See MyScrollToBottomObserver for details
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
           db.reference.child(ROOT).child(MESSAGES_CHILD)
               .child(getSmallerUserID(auth.uid.toString(), receiverUserID))
               .push()
               .setValue(friendlyMessage)
           messageEditText.setText("")
        }

        addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }
    }

    private fun getSmallerUserID(x: String, y: String): String {
        if (x < y) return x
        return y
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

    private fun onImageSelected(uri: Uri) {
        Log.d(TAG, "Uri: $uri")
        val user = auth.currentUser
        val tempMessage = FriendlyMessage(null, getUserName(), getPhotoUrl(), LOADING_IMAGE_URL)

        db.reference.child(ROOT).child(MESSAGES_CHILD)
            .child(getSmallerUserID(auth.uid.toString(), receiverUserID))
            .push()
            .setValue(
                tempMessage,
                DatabaseReference.CompletionListener { databaseError, databaseReference ->
                    if (databaseError != null) {
                        Log.w(
                            TAG, "Unable to write message to database.",
                            databaseError.toException()
                        )
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
                            .child(getSmallerUserID(auth.uid.toString(), receiverUserID))
                            .child(key!!)
                            .setValue(friendlyMessage)
                    }
            }
            .addOnFailureListener(this) { e ->
                Log.w(
                    TAG,
                    "Image upload task was unsuccessful.",
                    e
                )
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
                Log.e("TAG", "meet a IOOBE in RecyclerView")
            }
        }
    }

    companion object {
        private const val TAG = "ChatMainActivity"
        const val MESSAGES_CHILD = "messages"
        const val ROOT = "root"
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }
}