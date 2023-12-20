package com.developerspace.webrtcsample.util.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class UserData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "profile_url") val profileUrl: String?
)