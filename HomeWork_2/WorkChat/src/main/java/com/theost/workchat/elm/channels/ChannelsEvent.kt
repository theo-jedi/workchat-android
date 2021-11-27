package com.theost.workchat.elm.channels

import com.theost.workchat.data.models.ui.ListChannel
import com.theost.workchat.data.models.ui.ListTopic
import com.theost.workchat.ui.interfaces.DelegateItem

sealed class ChannelsEvent {
    sealed class Ui : ChannelsEvent() {
        object LoadChannels : Ui()

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
    }

    sealed class Internal : ChannelsEvent() {
        data class ItemsLoadingSuccess(val items: List<DelegateItem>) : Internal()
        data class ChannelsLoadingSuccess(val channels: List<ListChannel>, val subscribedChannels: List<Int>) : Internal()
        data class ChannelsSearchingSuccess(val channels: List<ListChannel>) : Internal()
        data class TopicsLoadingSuccess(val topics: List<ListTopic>) : Internal()
        data class DataLoadingError(val error: Throwable) : Internal()
    }
}
