package com.developerspace.webrtcsample.compose.ui.util

import com.developerspace.webrtcsample.R

data class Topic(var drawableID: Int = 0, var topicTitle: String = "")

val staticTopicList = arrayListOf(
    Topic(R.drawable.childhood, "Childhood"),
    Topic(R.drawable.hobby, "Hobby"),
    Topic(R.drawable.music, "Music"),
    Topic(R.drawable.food, "Food"),
    Topic(R.drawable.politics, "Politics"),
    Topic(R.drawable.sports, "Sports"),
    Topic(R.drawable.penpal, "Pen Pal"),
    Topic(R.drawable.relationships, "Relationship"),
    Topic(R.drawable.loneliness, "Loneliness"),
)