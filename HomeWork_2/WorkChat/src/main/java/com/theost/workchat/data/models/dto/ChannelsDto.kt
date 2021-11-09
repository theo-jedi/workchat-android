package com.theost.workchat.data.models.dto

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
    @SerialName("invite_only")
    val inviteOnly: Boolean,
    @SerialName("rendered_description")
    val renderedDescription: String,
    @SerialName("is_web_public")
    val isWebPublic: Boolean,
    @SerialName("stream_post_policy")
    val streamPostPolicy: Int,
    @SerialName("history_public_to_subscribers")
    val historyPublicToSubscribers: Boolean,
    @SerialName("first_message_id")
    val firstMessageId: Int,
    @SerialName("message_retention_days")
    val messageRetentionDays: String?,
    @SerialName("date_created")
    val dateCreated: Int,
    @SerialName("is_announcement_only")
    val isAnnouncementOnly: Boolean,
    @SerialName("color")
    val color: String? = null,
    @SerialName("is_muted")
    val isMuted: Boolean? = null,
    @SerialName("pin_to_top")
    val pinToTop: Boolean? = null,
    @SerialName("audible_notifications")
    val audibleNotifications: String? = null,
    @SerialName("desktop_notifications")
    val desktopNotifications: String? = null,
    @SerialName("email_notifications")
    val emailNotifications: String? = null,
    @SerialName("push_notifications")
    val pushNotifications: String? = null,
    @SerialName("wildcard_mentions_notify")
    val wildcardMentionsNotify: String? = null,
    @SerialName("role")
    val role: Int? = null,
    @SerialName("in_home_view")
    val inHomeView: Boolean? = null,
    @SerialName("stream_weekly_traffic")
    val streamWeeklyTraffic: String? = null,
    @SerialName("email_address")
    val emailAddress: String? = null
)

fun ChannelDto.mapToChannel(): Channel {
    return Channel(
        id = id,
        name = name,
        listOf()
    )
}