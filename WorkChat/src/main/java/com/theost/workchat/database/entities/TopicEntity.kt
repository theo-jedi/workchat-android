package com.theost.workchat.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theost.workchat.data.models.core.Topic

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey
    @ColumnInfo(name = "uid")
    val uid: String,
    @ColumnInfo(name = "topic_name")
    val name: String,
    @ColumnInfo(name = "channel_id")
    val channelId: Int
)

fun TopicEntity.mapToTopic(): Topic {
    return Topic(
        uid = uid,
        name = name,
        channelId = channelId
    )
}

fun Topic.mapToTopicEntity(): TopicEntity {
    return TopicEntity(
        uid = uid,
        name = name,
        channelId = channelId
    )
}