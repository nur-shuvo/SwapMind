package com.developerspace.webrtcsample.compose.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.developerspace.webrtcsample.compose.data.db.dao.RecentChatDao
import com.developerspace.webrtcsample.compose.data.db.dao.UserDao
import com.developerspace.webrtcsample.compose.data.preference.AppPref
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class SignOutWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val appPref: AppPref,
    private val recentChatDao: RecentChatDao,
    private val userDao: UserDao
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        Timber.i("doWork")
        appPref.clearALl()
        recentChatDao.deleteAllRecentChat()
        userDao.deleteAllUser()
        return Result.success()
    }

    companion object {
        fun enqueueWork(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<SignOutWorker>()
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}