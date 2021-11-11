package com.theost.workchat.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theost.workchat.data.models.core.Topic

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey
    @ColumnInfo(name = "topic_name")
    val name: String,
    @ColumnInfo(name = "channel_id")
    val channelId: Int,
    @ColumnInfo(name = "last_message_id")
    val lastMessageId: Int
)

fun TopicEntity.mapToTopic(): Topic {
    return Topic(
        name = name,
        lastMessageId = lastMessageId
    )
}

fun Topic.mapToTopicEntity(channelId: Int): TopicEntity {
    return TopicEntity(
        name = name,
        channelId = channelId,
        lastMessageId = lastMessageId
    )
}