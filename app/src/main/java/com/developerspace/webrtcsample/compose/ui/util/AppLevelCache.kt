package com.developerspace.webrtcsample.compose.ui.util

import com.developerspace.webrtcsample.model.User

// TODO Remove this class, instead rewrite logic so that userDB is sufficient.
object AppLevelCache {
    var userProfiles: MutableList<User>?= null
    var currentUserItemKey: Int = 0 // inedx of current user in cache list userProfiles
}