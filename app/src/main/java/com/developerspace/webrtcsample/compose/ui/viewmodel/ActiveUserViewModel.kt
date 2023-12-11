package com.developerspace.webrtcsample.compose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.developerspace.webrtcsample.ChatMainActivity
import com.developerspace.webrtcsample.MainActivity
import com.developerspace.webrtcsample.compose.ui.util.AppLevelCache
import com.developerspace.webrtcsample.model.User
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

class ActiveUserViewModel: ViewModel() {
    // ui states
    private val _userListState = MutableStateFlow<MutableList<User>>(mutableListOf())
    val userListState: StateFlow<MutableList<User>> = _userListState.asStateFlow()

    private var db: FirebaseDatabase = Firebase.database

    init {
        fetchAllUsers()
    }

    private fun fetchAllUsers() {
        db.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD).addValueEventListener(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue<MutableMap<String, User>>()?.let {
                        var cnt = 0
                        val resultList: MutableList<User> = mutableListOf()
                        it.forEach { entry ->
                            resultList.add(entry.value)
                            if (entry.key == Firebase.auth.uid) {
                                AppLevelCache.currentUserItemKey = cnt
                            }
                            cnt++
                        }
                        _userListState.value = resultList
                        AppLevelCache.userProfiles = resultList
                        Log.i(TAG, "total users - ${resultList.size}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // TODO("Not yet implemented")
                }

            }
        )
    }

    companion object {
        private const val TAG = "ActiveUserViewModel"
    }
}