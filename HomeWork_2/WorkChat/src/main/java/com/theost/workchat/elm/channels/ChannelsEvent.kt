package com.theost.workchat.elm.channels

import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.data.models.ui.ListChannel
import com.theost.workchat.data.models.ui.ListTopic

sealed class ChannelsEvent {
    sealed class Ui : ChannelsEvent() {
        data class LoadChannels(
            val channelsType: ChannelsType,
            val subscribedChannels: List<Int>
        ) : Ui()

        data class SearchChannels(
            val query: String
        ) : Ui()

        data class LoadTopics(
            val channelId: Int,
            val channelName: String
        ) : Ui()

        object HideTopics : Ui()

        data class OnChannelClick(
            val channelId: Int,
            val channelName: String,
            val isSelected: Boolean
        ) : Ui()

        data class OnTopicClick(
            val topicName: String
        ) : Ui()

        object Init : Ui()
    }

    sealed class Internal : ChannelsEvent() {
        data class ChannelsLoadingSuccess(val channels: List<ListChannel>) : Internal()
        data class ChannelsSearchingSuccess(val channels: List<ListChannel>) : Internal()
        data class TopicsLoadingSuccess(val topics: List<ListTopic>) : Internal()
        data class DataLoadingError(val error: Throwable) : Internal()
    }
}
