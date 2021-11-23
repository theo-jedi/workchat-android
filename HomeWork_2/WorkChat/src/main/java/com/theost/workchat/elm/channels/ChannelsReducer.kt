package com.theost.workchat.elm.channels

import android.util.Log
import com.theost.workchat.data.models.state.ResourceStatus
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class ChannelsReducer :
    DslReducer<ChannelsEvent, ChannelsState, ChannelsEffect, ChannelsCommand>() {
    override fun Result.reduce(event: ChannelsEvent): Any = when (event) {
        is ChannelsEvent.Internal.ChannelsLoadingSuccess -> {
            if (event.channels.isNotEmpty()) {
                state { copy(status = ResourceStatus.SUCCESS, channels = event.channels) }
                effects { +ChannelsEffect.HideLoading }
            } else {
                Log.d("channels_reducer", "Channels list is empty")
            }
        }
        is ChannelsEvent.Internal.ChannelsSearchingSuccess -> {
            state { copy(status = ResourceStatus.SUCCESS, searchedChannels = event.channels) }
            if (event.channels.isEmpty()) {
                effects { +ChannelsEffect.ShowEmpty }
            } else {
                effects { +ChannelsEffect.HideEmpty }
            }
        }
        is ChannelsEvent.Internal.TopicsLoadingSuccess -> {
            state { copy(status = ResourceStatus.SUCCESS, topics = event.topics) }
            effects { +ChannelsEffect.HideLoading }
        }
        is ChannelsEvent.Internal.DataLoadingError -> {
            state { copy(status = ResourceStatus.ERROR) }
            effects { +ChannelsEffect.ShowError }
        }
        is ChannelsEvent.Ui.LoadChannels -> {
            state { copy(status = ResourceStatus.LOADING) }
            commands { +ChannelsCommand.LoadChannels(event.channelsType, event.subscribedChannels) }
            effects { +ChannelsEffect.ShowLoading }
        }
        is ChannelsEvent.Ui.SearchChannels -> {
            if (state.status != ResourceStatus.LOADING) {
                if (event.query.isNotEmpty()) {
                    state {
                        copy(
                            status = ResourceStatus.SEARCHING,
                            searchedChannels = emptyList(),
                            isSearchEnabled = true
                        )
                    }
                    commands { +ChannelsCommand.SearchChannels(event.query, state.channels) }
                } else {
                    state {
                        copy(
                            status = ResourceStatus.LOADING,
                            searchedChannels = emptyList(),
                            isSearchEnabled = false
                        )
                    }
                    commands { +ChannelsCommand.RestoreChannels(state.channels) }
                    effects { +ChannelsEffect.HideEmpty }
                }
            } else {
                Log.d("channels_reducer", "Search is unavailable while loading data")
            }
        }
        is ChannelsEvent.Ui.LoadTopics -> {
            state { copy(status = ResourceStatus.LOADING, selectedChannelId = event.channelId, selectedChannelName = event.channelName) }
            commands { +ChannelsCommand.LoadTopics(event.channelId) }
        }
        is ChannelsEvent.Ui.HideTopics -> {
            state { copy(status = ResourceStatus.LOADING, selectedChannelId = -1, selectedChannelName = "") }
            commands { +ChannelsCommand.RestoreChannels(state.channels) }
        }
        is ChannelsEvent.Ui.OnChannelClick -> {
            effects { +ChannelsEffect.OnChannelClick(event.channelId, event.channelName, event.isSelected) }
        }
        is ChannelsEvent.Ui.OnTopicClick -> {
            effects { +ChannelsEffect.OnTopicClick(state.selectedChannelName, event.topicName) }
        }
        is ChannelsEvent.Ui.Init -> {
        }
    }
}