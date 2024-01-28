package com.developerspace.webrtcsample

import android.app.Application
import android.util.Log
import com.developerspace.webrtcsample.compose.util.misc.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SwapMindApp: Application() {
    companion object {
        private const val TAG = "SwapMindApp"
    }
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}