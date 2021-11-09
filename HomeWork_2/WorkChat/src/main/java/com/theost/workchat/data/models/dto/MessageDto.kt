package com.theost.workchat.data.models.dto

import com.theost.workchat.data.models.core.Message
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.utils.DateUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetMessagesResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String,
    @SerialName("anchor")
    val anchor: Long,
    @SerialName("found_anchor")
    val foundAnchor: Boolean,
    @SerialName("found_newest")
    val foundNewest: Boolean,
    @SerialName("messages")
    val messages: List<MessageDto>
)

@Serializable
data class MessageDto(
    @SerialName("id")
    val id: Int,
    @SerialName("sender_full_name")
    val name: String,
    @SerialName("content")
    val content: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("timestamp")
    val time: Int,
    @SerialName("reactions")
    val reactions: List<ReactionDto>,
    @SerialName("is_me_message")
    val isMyMessage: Boolean,
    @SerialName("sender_id")
    val senderId: Int,
    @SerialName("recipient_id")
    val recipientId: Int,
    @SerialName("client")
    val client: String,
    @SerialName("subject")
    val subject: String,
    @SerialName("topic_links")
    val topicsLinks: List<String>,
    @SerialName("submessages")
    val subMessages: List<String>,
    @SerialName("flags")
    val flags: List<String>,
    @SerialName("sender_email")
    val email: String,
    @SerialName("sender_realm_str")
    val realmStr: String,
    @SerialName("type")
    val type: String,
    @SerialName("stream_id")
    val streamId: Int? = null,
    @SerialName("content_type")
    val contentType: String
)

@Serializable
data class ReactionDto(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("emoji_name")
    val name: String,
    @SerialName("emoji_code")
    val code: String,
    @SerialName("reaction_type")
    val reactionType: String,
    @SerialName("user")
    val user: ReactionUserDto
)

@Serializable
data class ReactionUserDto(
    @SerialName("id")
    val id: Int,
    @SerialName("full_name")
    val name: String,
    @SerialName("email")
    val email: String
)

fun MessageDto.mapToMessage(): Message {
    return Message(
        id = id,
        name = name,
        text = content,
        avatarUrl = avatarUrl,
        time = DateUtils.utcToDate(time),
        reactions = reactions.map { it.mapToReaction()
    },
        messageType = if (isMyMessage)
    MessageType.OUTCOME else MessageType.INCOME
    )
}

fun ReactionDto.mapToReaction(): String {
    return String(Character.toChars(Integer.parseInt(code, 16)))
}