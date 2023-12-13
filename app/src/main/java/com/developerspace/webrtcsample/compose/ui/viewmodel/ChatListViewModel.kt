package com.developerspace.webrtcsample.compose.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.developerspace.webrtcsample.ChatMainActivity
import com.developerspace.webrtcsample.MainActivity
import com.developerspace.webrtcsample.model.FriendlyMessage
import com.developerspace.webrtcsample.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RecentMessage(
    var toUserId: String = "",
    var toUserName: String = "",
    var toPhotoUrl: String = "",
    var friendlyMessage: FriendlyMessage = FriendlyMessage()
)

class ChatListViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    val uidVsUserName: MutableMap<String, String> = mutableMapOf()
    val uidVsPhotoUrl: MutableMap<String, String> = mutableMapOf()

    // ui states
    private val _recentMessageListState =
        MutableStateFlow<MutableList<RecentMessage>>(mutableListOf())
    val recentMessageListState: StateFlow<MutableList<RecentMessage>> =
        _recentMessageListState.asStateFlow()

    private var db: FirebaseDatabase = Firebase.database

    init {
        // fetch for userNames
        // TODO optimize by using AppLevelCache
        db.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD).get()
            .addOnSuccessListener { snapShot ->
                snapShot.getValue<MutableMap<String, User>>()?.let {
                    it.forEach { entry ->
                        uidVsUserName[entry.key] = entry.value.userName!!
                        uidVsPhotoUrl[entry.key] = entry.value.photoUrl!!
                    }
                    // observe now
                    observeForRecentChanges()
                }
            }
    }

    private fun observeForRecentChanges() {
        db.reference.child(ChatMainActivity.ROOT).child(ChatMainActivity.MESSAGES_CHILD).child(
            ChatMainActivity.RECENT_MESSAGES_CHILD
        ).child(auth.uid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recentMessages: MutableList<RecentMessage> = mutableListOf()
                snapshot.getValue<MutableMap<String, FriendlyMessage>>()?.forEach { entry ->
                    val recentMessageTemp = RecentMessage()
                    recentMessageTemp.toUserId = entry.key
                    recentMessageTemp.friendlyMessage = entry.value
                    recentMessageTemp.toUserName =
                        uidVsUserName[recentMessageTemp.toUserId]!!
                    recentMessageTemp.toPhotoUrl = uidVsPhotoUrl[recentMessageTemp.toUserId]!!
                    recentMessages.add(recentMessageTemp)
                    _recentMessageListState.value = recentMessages
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO("Not yet implemented")
            }
        })
    }
}