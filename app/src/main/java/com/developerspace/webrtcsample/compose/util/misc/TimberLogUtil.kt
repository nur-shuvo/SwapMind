package com.developerspace.webrtcsample.compose.util.misc

import android.util.Log
import timber.log.Timber

class ReleaseTree : @org.jetbrains.annotations.NotNull Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR || priority == Log.WARN){
            //SEND ERROR REPORTS TO SEVER.
        }
    }
}