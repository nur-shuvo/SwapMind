package com.developerspace.webrtcsample.compose.worker

import android.content.Context
import android.os.Bundle
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.developerspace.webrtcsample.compose.data.db.dao.RecentChatDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber

/**
 * Recent Chat worker class to insert or update db in background
 */
@HiltWorker
class UpsertRecentChatWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val recentChatDao: RecentChatDao
): CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        Timber.i("UpsertRecentChatWorker doWork")
        val workType = inputData.getInt("workType", -1)
        when (workType) {
            INSERT -> {
                // TODO: need to implement
            }
            UPDATE -> {
                // TODO: need to implement
            }
            RESET_UNREAD_COUNT -> {
                inputData.getString("toUserId")?.let { toUserId->
                    recentChatDao.resetUnreadCount(toUserId)
                }
            }
            else -> {
                Timber.i("UpsertRecentChatWorker workType not matched")
            }
        }.also {
            Timber.i("Work Type: $workType ")
        }
        return Result.success()
    }

    companion object {
        fun enqueueWork(context: Context, workType: Int, bundle: Bundle) {
            val inputData = Data.Builder()
                .putInt("workType", workType)
                .apply {
                    bundle.keySet().forEach { key->
                        putString(key, bundle.getString(key))
                    }
                }
                .build()

            val workRequest = OneTimeWorkRequestBuilder<UpsertRecentChatWorker>()
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }

        const val INSERT = 0
        const val UPDATE = 1
        const val RESET_UNREAD_COUNT = 2
    }
}