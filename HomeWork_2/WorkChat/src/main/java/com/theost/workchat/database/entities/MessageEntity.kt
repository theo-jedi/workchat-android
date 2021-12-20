package com.theost.workchat.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theost.workchat.data.models.core.Message
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.util.*

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "time")
    val time: Long,
    @ColumnInfo(name = "sender_id")
    val senderId: Int,
    @ColumnInfo(name = "sender_name")
    val senderName: String,
    @ColumnInfo(name = "avatar_url")
    val senderAvatarUrl: String,
    @ColumnInfo(name = "reactions")
    val reactions: String,
    @ColumnInfo(name = "channel_name")
    val channelName: String,
    @ColumnInfo(name = "topic_name")
    val topicName: String
)

fun MessageEntity.mapToMessage(): Message {
    return Message(
        id = id,
        content = content,
        time = Date(time),
        senderId = senderId,
        senderName = senderName,
        senderAvatarUrl = senderAvatarUrl,
        reactions = Json.decodeFromString(serializer(), reactions)
    )
}

fun Message.mapToMessageEntity(channelName: String, topicName: String): MessageEntity {
    return MessageEntity(
        id = id,
        content = content,
        time = time.time,
        senderId = senderId,
        senderName = senderName,
        senderAvatarUrl = senderAvatarUrl,
        reactions = Json.encodeToString(serializer(), reactions),
        channelName = channelName,
        topicName = topicName
    )
}