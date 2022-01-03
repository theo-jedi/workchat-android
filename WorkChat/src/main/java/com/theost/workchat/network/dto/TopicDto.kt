package com.theost.workchat.network.dto

import com.theost.workchat.data.models.core.Topic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetTopicsResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String,
    @SerialName("topics")
    val topics: List<TopicDto>
)

@Serializable
data class TopicDto(
    @SerialName("name")
    val name: String,
    @SerialName("max_id")
    val lastMessageId: Int
)

fun TopicDto.mapToTopic(channelId: Int) : Topic {
    return Topic(
        uid = "${channelId}_$name",
        name = name,
        channelId = channelId
    )
}