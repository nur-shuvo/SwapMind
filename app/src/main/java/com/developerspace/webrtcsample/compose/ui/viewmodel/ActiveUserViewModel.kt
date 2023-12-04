package com.developerspace.webrtcsample.compose.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.developerspace.webrtcsample.ChatMainActivity
import com.developerspace.webrtcsample.MainActivity
import com.developerspace.webrtcsample.model.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.function.Consumer

private const val TAG = "ActiveUserViewModel"

class ActiveUserViewModel: ViewModel() {

    // StateFLow
    private val _userListState = MutableStateFlow<MutableList<User>>(mutableListOf())
    val userListState: StateFlow<MutableList<User>> = _userListState.asStateFlow()

    private lateinit var db: FirebaseDatabase

    init {
        getAllUsers()
    }

    private fun getAllUsers() {
        val itemList: MutableList<User> = mutableListOf()
        db = Firebase.database
        db.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD).get()
            .addOnSuccessListener { snapshot ->
                snapshot.getValue<MutableMap<String, User>>()?.let {
                    it.forEach { entry ->
                        itemList.add(entry.value)
                    }
                }
                Log.i(TAG, "itemList size - ${itemList.size}")
                _userListState.value = itemList
            }
    }
}