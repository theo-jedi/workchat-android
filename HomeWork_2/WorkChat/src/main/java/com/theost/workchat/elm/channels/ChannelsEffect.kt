package com.theost.workchat.elm.channels

sealed class ChannelsEffect {
    object ShowError : ChannelsEffect()
    object ShowLoading : ChannelsEffect()
    object HideLoading : ChannelsEffect()
    object ShowEmpty : ChannelsEffect()
    object HideEmpty : ChannelsEffect()

    data class OnChannelClicked(
        val channelId: Int,
        val channelName: String,
        val isSelected: Boolean
    ) : ChannelsEffect()

    data class OnTopicClicked(
        val channelName: String,
        val topicName: String
    ) : ChannelsEffect()
}
