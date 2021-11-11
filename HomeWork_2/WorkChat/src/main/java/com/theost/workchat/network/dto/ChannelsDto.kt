package com.theost.workchat.network.dto

import com.theost.workchat.data.models.core.Channel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetChannelsResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String,
    @SerialName("streams")
    val channels: List<ChannelDto>
)

@Serializable
data class GetSubscribedChannelsResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String,
    @SerialName("subscriptions")
    val channels: List<ChannelDto>
)

@Serializable
data class ChannelDto(
    @SerialName("stream_id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("color")
    val color: String? = null,
    @SerialName("is_muted")
    val isMuted: Boolean? = null,
    @SerialName("pin_to_top")
    val pinToTop: Boolean? = null
)

fun ChannelDto.mapToChannel(): Channel {
    return Channel(
        id = id,
        name = name
    )
}