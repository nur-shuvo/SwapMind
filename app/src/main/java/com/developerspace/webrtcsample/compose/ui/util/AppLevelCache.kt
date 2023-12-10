package com.developerspace.webrtcsample.compose.ui.util

import com.developerspace.webrtcsample.model.User

object AppLevelCache {
    var userProfiles: List<User>?= null
    var currentUserItemKey: Int = 0
}