package com.developerspace.webrtcsample.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.developerspace.webrtcsample.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UpdateTopicOnlineUserWorker @Inject constructor(
    @ApplicationContext private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val gson = Gson()
        val topicTitle = inputData.getString("topicTitle")
        val userJson = inputData.getString("user")
        val user = gson.fromJson(userJson, User::class.java)
        val updateType = inputData.getInt("updateType", ADD)
        if (topicTitle?.isNotEmpty() == true && user?.userID?.isNotEmpty() == true) {
            val db = Firebase.database
            when (updateType) {
                ADD -> {
                    db.reference.child(ChatMainActivity.ROOT).child(TOPIC_PATH)
                        .child(topicTitle).child(ACTIVE_USERS_PATH).child(user.userID!!)
                        .setValue(user)
                }

                DELETE -> {
                    db.reference.child(ChatMainActivity.ROOT).child(TOPIC_PATH)
                        .child(topicTitle).child(ACTIVE_USERS_PATH).child(user.userID!!)
                        .removeValue()
                }
            }
        }
        return Result.success()
    }

    companion object {
        fun enqueueWork(context: Context, type: Int, user: User, topicTitle: String) {
            val gson = Gson()
            val inputData = Data.Builder()
                .putInt("updateType", type)
                .putString("user", gson.toJson(user))
                .putString("topicTitle", topicTitle)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<UpdateTopicOnlineUserWorker>()
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }

        const val ADD = 0
        const val DELETE = 1
        const val TOPIC_PATH = "topic"
        const val ACTIVE_USERS_PATH = "active-users"
    }

}