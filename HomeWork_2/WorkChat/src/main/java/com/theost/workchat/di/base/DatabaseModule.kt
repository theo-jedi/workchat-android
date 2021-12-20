package com.theost.workchat.di.base

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.theost.workchat.database.dao.*
import com.theost.workchat.database.db.CacheDatabase
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideCacheDatabase(context: Context): CacheDatabase {
        return Room.databaseBuilder(context, CacheDatabase::class.java, "cache_database")
            .setQueryCallback({ sqlQuery, bindArgs ->
                Log.d(
                    "cache_database",
                    "SQL Query: $sqlQuery SQL Args: $bindArgs"
                )
            }, Executors.newSingleThreadExecutor()).build()
    }

    @Provides
    @Singleton
    fun provideUsersDao(database: CacheDatabase): UsersDao = database.usersDao()

    @Provides
    @Singleton
    fun provideChannelsDao(database: CacheDatabase): ChannelsDao = database.channelsDao()

    @Provides
    @Singleton
    fun provideTopicsDao(database: CacheDatabase): TopicsDao = database.topicsDao()

    @Provides
    @Singleton
    fun provideMessagesDao(database: CacheDatabase): MessagesDao = database.messagesDao()

    @Provides
    @Singleton
    fun provideReactionsDao(database: CacheDatabase): ReactionsDao = database.reactionsDao()

}