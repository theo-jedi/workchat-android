package com.theost.workchat.elm.channels

import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListChannel
import com.theost.workchat.data.models.ui.ListTopic
import com.theost.workchat.ui.interfaces.DelegateItem

data class ChannelsState(
    val status: ResourceStatus = ResourceStatus.LOADING,
    val channelsType: ChannelsType = ChannelsType.ALL,
    val items: List<DelegateItem> = emptyList(),
    val channels: List<ListChannel> = emptyList(),
    val topics: List<ListTopic> = emptyList(),
    val selectedChannelId: Int = -1,
    val selectedChannelName: String = "",
    val subscribedChannels: List<Int> = emptyList(),
    val searchedChannels: List<ListChannel> = emptyList(),
    val isSearchEnabled: Boolean = false
)