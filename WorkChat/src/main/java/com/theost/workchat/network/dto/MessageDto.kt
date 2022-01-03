package com.theost.workchat.network.dto

import com.theost.workchat.data.models.core.Message
import com.theost.workchat.utils.DateUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetMessagesResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String,
    @SerialName("messages")
    val messages: List<MessageDto>
)

@Serializable
data class DeleteMessageResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String
)

@Serializable
data class MessageDto(
    @SerialName("id")
    val id: Int,
    @SerialName("content")
    val content: String,
    @SerialName("timestamp")
    val timestamp: Int,
    @SerialName("sender_id")
    val senderId: Int,
    @SerialName("sender_full_name")
    val senderName: String,
    @SerialName("avatar_url")
    val senderAvatarUrl: String,
    @SerialName("reactions")
    val reactions: List<ReactionDto>
)

fun MessageDto.mapToMessage(): Message {
    return Message(
        id = id,
        content = content,
        date = DateUtils.utcToDate(timestamp),
        senderId = senderId,
        senderName = senderName,
        senderAvatarUrl = senderAvatarUrl,
        reactions = reactions.map { it.mapToReaction() }
    )
}