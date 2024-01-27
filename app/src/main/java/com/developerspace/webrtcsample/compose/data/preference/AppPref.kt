package com.developerspace.webrtcsample.compose.data.preference

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPref @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE)

    fun setFcmDeviceToken(token: String) {
        with(pref.edit()) {
            putString(KEY_FCM_DEVICE_TOKEN, token)
            apply()
        }
    }

    fun getFcmDeviceToken(): String {
        return pref.getString(KEY_FCM_DEVICE_TOKEN, "") ?: ""
    }

    companion object {
        const val KEY_FCM_DEVICE_TOKEN = "KEY_FCM_DEVICE_TOKEN"
    }
}