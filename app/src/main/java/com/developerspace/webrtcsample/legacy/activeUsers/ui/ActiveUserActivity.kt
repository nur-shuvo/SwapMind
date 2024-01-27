package com.developerspace.webrtcsample.legacy.activeUsers.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.developerspace.webrtcsample.legacy.MainActivity
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.data.model.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ActiveUserActivity : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.active_users)

        val itemList : MutableList<User> = mutableListOf()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = Firebase.database
        db.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD).get().addOnSuccessListener { snapshot ->
            snapshot.getValue<MutableMap<String, User>>()?.let {
                it.forEach { entry ->
                    itemList.add(entry.value)
                }
            }

            // load data in recyclerview
            val adapter = ItemAdapter(itemList)
            recyclerView.adapter = adapter
        }
    }
}