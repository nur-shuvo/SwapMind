package com.developerspace.webrtcsample.util.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Upsert
    suspend fun upsertUserProfileData(userData: UserData)

    @Query("SELECT * FROM user_data WHERE user_id = :userId")
    fun getUserProfileData(userId: String): Flow<UserData>
}