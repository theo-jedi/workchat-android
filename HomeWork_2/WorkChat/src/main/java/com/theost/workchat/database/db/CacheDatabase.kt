package com.theost.workchat.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.theost.workchat.database.dao.*
import com.theost.workchat.database.entities.*

@Database(
    entities = [
        UserEntity::class,
        PresenceEntity::class,
        ChannelEntity::class,
        TopicEntity::class,
        MessageEntity::class,
        ReactionEntity::class
    ],
    exportSchema = false,
    version = 1
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun presenceDao(): PresenceDao
    abstract fun channelsDao(): ChannelsDao
    abstract fun topicsDao(): TopicsDao
    abstract fun messagesDao(): MessagesDao
    abstract fun reactionsDao(): ReactionsDao
}