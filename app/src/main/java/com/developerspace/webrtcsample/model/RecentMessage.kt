package com.developerspace.webrtcsample.model

data class RecentMessage(
    var toUserId: String = "",
    var toUserName: String = "",
    var toPhotoUrl: String = "",
    var friendlyMessage: FriendlyMessage = FriendlyMessage()
)
