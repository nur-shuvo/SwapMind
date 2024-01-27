package com.developerspace.webrtcsample.compose.data.repository

import android.util.Log
import androidx.core.util.Consumer
import com.developerspace.webrtcsample.compose.ui.util.Topic
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.developerspace.webrtcsample.legacy.MainActivity
import com.developerspace.webrtcsample.compose.data.model.User
import com.developerspace.webrtcsample.compose.data.db.dao.UserDao
import com.developerspace.webrtcsample.compose.data.db.entity.UserData
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
import timber.log.Timber
import javax.inject.Inject

class UserListRepository @Inject constructor(private val userDao: UserDao) {
    private var db: FirebaseDatabase = Firebase.database
    private val workerScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "UserListRepository"
    }

    fun getUserListFromDbFlow(): Flow<List<User>> {
        return userDao.getAllUser().map { userDataList ->
            userDataList.map { userData ->
                User(userData.userId, userData.userName, userData.profileUrl, userData.onlineStatus)
            }
        }
    }

    fun getUserByUserID(userID: String): Flow<UserData> {
        return userDao.getUserProfileData(userID)
    }

    fun fetchAllUsersRemote() {
        db.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue<MutableMap<String, User>>()?.let {
                            val resultList: MutableList<User> = mutableListOf()
                            it.forEach { entry ->
                                val user = entry.value
                                resultList.add(user)
                                saveUserInDb(user)
                            }
                            Log.i(TAG, "total users - ${resultList.size}")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // TODO("Not yet implemented")
                    }
                }
            )
    }

    fun fetchAllActiveUsersByTopic(topic: Topic, consumer: Consumer<List<User>>) {
        // TODO db path will change based on topic
        db.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue<MutableMap<String, User>>()?.let {
                            val resultList: MutableList<User> = mutableListOf()
                            it.forEach { entry ->
                                val user = entry.value
                                resultList.add(user)
                                saveUserInDb(user)
                            }
                            Timber.i("total users - ${resultList.size}")
                            consumer.accept(resultList)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // TODO("Not yet implemented")
                    }
                }
            )
    }

    private fun saveUserInDb(user: User) {
        workerScope.launch {
            userDao.insertUser(
                UserData(
                    user.userID!!,
                    user.userName,
                    user.photoUrl,
                    user.onlineStatus
                )
            )
        }
    }
}