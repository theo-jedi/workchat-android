package com.theost.workchat.elm.channels

import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.data.models.ui.ListChannel
import com.theost.workchat.data.models.ui.ListTopic

sealed class ChannelsCommand {
    data class LoadChannels(
        val channelsType: ChannelsType,
        val subscribedChannels: List<Int>,
        val selectedChannelId: Int
    ) : ChannelsCommand()

    data class SearchChannels(
        val query: String,
        val channels: List<ListChannel>
    ) : ChannelsCommand()

    data class LoadTopics(
        val channelId: Int,
        val channels: List<ListChannel>,
        val selectedChannelId: Int
    ) : ChannelsCommand()

    data class LoadItems(
        val channels: List<ListChannel>,
        val topics: List<ListTopic>,
        val selectedChannelId: Int
    ) : ChannelsCommand()
}
