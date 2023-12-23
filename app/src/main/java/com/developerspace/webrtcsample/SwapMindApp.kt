package com.developerspace.webrtcsample

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SwapMindApp: Application() {
    companion object {
        private const val TAG = "SwapMindApp"
    }
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
    }
}