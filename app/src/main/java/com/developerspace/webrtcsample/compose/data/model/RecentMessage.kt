package com.developerspace.webrtcsample.compose.data.model

data class RecentMessage(
    var toUserId: String = "",
    var toUserName: String = "",
    var toPhotoUrl: String = "",
    val unreadCount: Int = 0,
    var friendlyMessage: FriendlyMessage = FriendlyMessage()
)
