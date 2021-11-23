package com.theost.workchat.elm.channels

import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.data.models.ui.ListChannel

sealed class ChannelsCommand {
    data class LoadChannels(
        val channelsType: ChannelsType,
        val subscribedChannels: List<Int>
    ) : ChannelsCommand()

    data class SearchChannels(
        val query: String,
        val channels: List<ListChannel>
    ) : ChannelsCommand()

    data class RestoreChannels(
        val channels: List<ListChannel>
    ) : ChannelsCommand()

    data class LoadTopics(
        val channelId: Int
    ) : ChannelsCommand()
}
