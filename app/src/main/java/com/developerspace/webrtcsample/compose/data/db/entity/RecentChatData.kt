package com.developerspace.webrtcsample.compose.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_chat_data")
data class RecentChatData(
    @PrimaryKey @ColumnInfo(name = "receiver_user_id") val toUserId: String = "",
    @ColumnInfo(name = "receiver_user_name") val toUserName: String = "",
    @ColumnInfo(name = "receiver_photo_url") val toPhotoUrl: String = "",
    @Embedded val messageData: MessageData?
)

data class MessageData(
    @ColumnInfo(name = "text") val text: String? = null,
    @ColumnInfo(name = "sender_name") val senderName: String? = null,
    @ColumnInfo(name = "sender_photo_url") val senderPhotoUrl: String? = null,
    @ColumnInfo(name = "image_message_url") val imageUrl: String? = null,
    @ColumnInfo(name = "time") val time: String? = null
)