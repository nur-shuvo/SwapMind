package com.developerspace.webrtcsample

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.developerspace.webrtcsample.compose.util.misc.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SwapMindApp: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory : HiltWorkerFactory
    companion object {
        private const val TAG = "SwapMindApp"
    }
    override fun onCreate() {
        super.onCreate()
        Timber.i(TAG, "onCreate")

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}