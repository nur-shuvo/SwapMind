package com.developerspace.webrtcsample.compose.ui.util

import com.developerspace.webrtcsample.model.User

object AppLevelCache {
    var userProfiles: MutableList<User>?= null
    var currentUserItemKey: Int = 0
}