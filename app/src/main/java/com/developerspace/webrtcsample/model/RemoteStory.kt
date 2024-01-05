package com.developerspace.webrtcsample.model

const val STORIES_REMOTE_PATH = "stories"

data class RemoteStory(
    var userID: String? = "",
    var storyUrl: String = "",
    var postTimeStamp: String = "",
    var extraData: String = "",
)