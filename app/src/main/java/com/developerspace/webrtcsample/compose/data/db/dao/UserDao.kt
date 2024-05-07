package com.developerspace.webrtcsample.compose.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.developerspace.webrtcsample.compose.data.db.entity.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userData: UserData)

    @Query(value = "SELECT * FROM user_data")
    fun getAllUser(): Flow<List<UserData>>

    @Query("SELECT * FROM user_data WHERE user_id = :userId")
    fun getUserProfileData(userId: String): Flow<UserData>

    @Delete
    suspend fun deleteUser(userData: UserData)

    @Query("DELETE FROM user_data")
    suspend fun deleteAllUser()
}