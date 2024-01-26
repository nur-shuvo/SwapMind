package com.developerspace.webrtcsample.compose.data.repository

import android.util.Log
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.developerspace.webrtcsample.legacy.MainActivity
import com.developerspace.webrtcsample.model.FriendlyMessage
import com.developerspace.webrtcsample.model.RecentMessage
import com.developerspace.webrtcsample.model.User
import com.developerspace.webrtcsample.compose.data.db.dao.RecentChatDao
import com.developerspace.webrtcsample.compose.data.db.dao.UserDao
import com.developerspace.webrtcsample.compose.data.db.entity.MessageData
import com.developerspace.webrtcsample.compose.data.db.entity.RecentChatData
import com.developerspace.webrtcsample.compose.data.db.entity.UserData
import com.developerspace.webrtcsample.util.misc.TimeUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val userDao: UserDao,
    private val recentChatDao: RecentChatDao
) {
    companion object {
        private const val TAG = "ChatRepository"
    }

    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseDatabase = Firebase.database
    private val workerScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun getChatListFromDbFlow(): Flow<List<RecentMessage>> {
        Log.i(TAG,"getChatListFromDbFlow")
        val res = recentChatDao.getRecentChatList().map { recentChatList ->
            recentChatList.map { recentChatData ->
                RecentMessage(
                    recentChatData.toUserId,
                    recentChatData.toUserName,
                    recentChatData.toPhotoUrl,
                    FriendlyMessage(
                        recentChatData.messageData?.text,
                        recentChatData.messageData?.senderName,
                        recentChatData.messageData?.senderPhotoUrl,
                        recentChatData.messageData?.imageUrl,
                        recentChatData.messageData?.time?.let {
                            TimeUtil.formatMillisecondToHourMinute(it)
                        },
                    )
                )
            }
        }
        Log.i(TAG,"getChatListFromDbFlow done")
        return res
    }

    suspend fun fetchChatListRemote() {
        Log.i(TAG,"fetchChatListRemote")
        db.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(TAG, "fetchChatListRemote onDataChange")
                    snapshot.getValue<MutableMap<String, User>>()?.forEach { entry ->
                        workerScope.launch {
                            userDao.insertUser(
                                UserData(
                                    entry.key,
                                    entry.value.userName,
                                    entry.value.photoUrl,
                                    entry.value.onlineStatus,
                                )
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // TODO("Not yet implemented")
                }
            })

        observeForRecentChanges()
    }

    private fun observeForRecentChanges() {
        Log.i(TAG,"observeForRecentChanges started")
        db.reference.child(ChatMainActivity.ROOT).child(ChatMainActivity.MESSAGES_CHILD)
            .child(ChatMainActivity.RECENT_MESSAGES_CHILD).child(auth.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(TAG, "observeForRecentChanges onDataChange")
                    snapshot.getValue<MutableMap<String, FriendlyMessage>>()?.forEach { entry ->
                        workerScope.launch {
                            userDao.getUserProfileData(entry.key).collect { userData ->
                                Log.i(TAG, "UserData collected")
                                val recentChatData = RecentChatData(
                                    entry.key,
                                    userData.userName!!,
                                    userData.profileUrl!!,
                                    MessageData(
                                        entry.value.text,
                                        entry.value.name,
                                        entry.value.photoUrl,
                                        entry.value.imageUrl,
                                        entry.value.time,
                                    )
                                )
                                saveRecentChatInDb(recentChatData)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // TODO("Not yet implemented")
                }
            })
    }


    private suspend fun saveRecentChatInDb(recentChatData: RecentChatData) {
        recentChatDao.insertRecentChat(
            RecentChatData(
                recentChatData.toUserId,
                recentChatData.toUserName,
                recentChatData.toPhotoUrl,
                recentChatData.messageData
            )
        )
    }
}