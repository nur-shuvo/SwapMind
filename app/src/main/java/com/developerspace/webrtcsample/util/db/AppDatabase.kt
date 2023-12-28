package com.developerspace.webrtcsample.util.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.developerspace.webrtcsample.compose.ui.viewmodel.RecentMessage
import com.developerspace.webrtcsample.util.db.dao.UserDao
import com.developerspace.webrtcsample.util.db.entity.UserData

@Database(
    entities = [UserData::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}