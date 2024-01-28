package com.developerspace.webrtcsample.compose.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.developerspace.webrtcsample.compose.data.db.dao.RecentChatDao
import com.developerspace.webrtcsample.compose.data.db.dao.UserDao
import com.developerspace.webrtcsample.compose.data.db.entity.RecentChatData
import com.developerspace.webrtcsample.compose.data.db.entity.UserData

@Database(
    entities = [UserData::class, RecentChatData::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recentChatDao(): RecentChatDao
}