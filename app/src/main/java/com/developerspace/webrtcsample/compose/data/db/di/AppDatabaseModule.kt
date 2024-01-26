package com.developerspace.webrtcsample.compose.data.db.di

import android.content.Context
import androidx.room.Room
import com.developerspace.webrtcsample.util.Constants.APP_DATABASE_NAME
import com.developerspace.webrtcsample.compose.data.db.AppDatabase
import com.developerspace.webrtcsample.compose.data.db.dao.RecentChatDao
import com.developerspace.webrtcsample.compose.data.db.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            APP_DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideRecentChatDao(db: AppDatabase): RecentChatDao {
        return db.recentChatDao()
    }
}