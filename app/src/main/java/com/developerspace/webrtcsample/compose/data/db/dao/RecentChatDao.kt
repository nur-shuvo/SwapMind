package com.developerspace.webrtcsample.compose.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.developerspace.webrtcsample.compose.data.db.entity.RecentChatData
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentChat(recentChatData: RecentChatData)

    @Query("SELECT * FROM recent_chat_data WHERE receiver_user_id = :chatWithUserId")
    suspend fun getRecentChat(chatWithUserId: String): RecentChatData?

    @Query("SELECT * FROM recent_chat_data ORDER BY time DESC")
    fun getRecentChatList(): Flow<List<RecentChatData>>

    @Update
    suspend fun updateRecentChat(recentChatData: RecentChatData)

    @Query("UPDATE recent_chat_data SET unread_count = 0 WHERE receiver_user_id = :chatWithUserId")
    suspend fun resetUnreadCount(chatWithUserId: String)

    @Delete
    suspend fun deleteRecentChat(recentChatData: RecentChatData)

    @Query("DELETE FROM recent_chat_data")
    suspend fun deleteAllRecentChat()
}