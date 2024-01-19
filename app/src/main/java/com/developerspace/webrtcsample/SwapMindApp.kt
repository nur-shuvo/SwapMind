package com.developerspace.webrtcsample

import android.app.Application
import android.os.Build
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import com.developerspace.webrtcsample.BuildConfig
import com.developerspace.webrtcsample.util.misc.ReleaseTree

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