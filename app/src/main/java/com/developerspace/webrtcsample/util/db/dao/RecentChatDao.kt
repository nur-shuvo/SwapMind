package com.developerspace.webrtcsample.util.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.developerspace.webrtcsample.util.db.entity.RecentChatData
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentChat(recentChatData: RecentChatData)

    @Query("SELECT * FROM recent_chat_data ORDER BY time DESC")
    fun getRecentChatList(): Flow<List<RecentChatData>>
    @Delete
    suspend fun deleteRecentChat(recentChatData: RecentChatData)

    @Query("DELETE FROM recent_chat_data")
    suspend fun deleteAllUser()
}