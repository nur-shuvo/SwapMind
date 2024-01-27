package com.developerspace.webrtcsample.compose.data.repository

import android.util.Log
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.developerspace.webrtcsample.compose.data.model.RemoteStory
import com.developerspace.webrtcsample.compose.data.model.STORIES_REMOTE_PATH
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class RemoteStoriesRepository @Inject constructor() {
    private var db: FirebaseDatabase = Firebase.database

    fun fetchAllStoriesRemote(callback: (List<RemoteStory>) -> Unit) {
        Log.i(TAG, "fetchAllStoriesRemote start")
        val resultListStory: MutableList<RemoteStory> = mutableListOf()
        db.reference.child(ChatMainActivity.ROOT).child(STORIES_REMOTE_PATH)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.i(TAG, "fetchAllStoriesRemote data received")
                        snapshot.getValue<MutableMap<String, RemoteStory>>()?.let { returnedMap ->
                            // filter list so that my own story comes first
                            var myRemoteStory: RemoteStory? = null
                            Firebase.auth.uid?.let {
                                myRemoteStory = returnedMap[Firebase.auth.uid!!]
                                myRemoteStory?.let { my -> resultListStory.add(my) }
                            }
                            returnedMap.entries.forEach {
                                if (it.key != Firebase.auth.uid) {
                                    resultListStory.add(it.value)
                                }
                            }
                            callback.invoke(resultListStory)
                            resultListStory.clear()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // TODO("Not yet implemented")
                    }
                }
            )
    }

    companion object {
        private const val TAG = "RemoteStoriesRepository"
    }
}