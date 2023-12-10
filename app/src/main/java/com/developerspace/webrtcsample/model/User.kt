package com.developerspace.webrtcsample.model

data class User(
    var userID: String? = "",
    var userName: String? = "",
    var photoUrl: String? = "",
    var onlineStatus: Boolean? = false
)