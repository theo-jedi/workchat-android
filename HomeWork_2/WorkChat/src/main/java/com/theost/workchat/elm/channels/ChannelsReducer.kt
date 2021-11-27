package com.theost.workchat.elm.channels

import android.util.Log
import com.theost.workchat.data.models.state.ResourceStatus
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class ChannelsReducer :
    DslReducer<ChannelsEvent, ChannelsState, ChannelsEffect, ChannelsCommand>() {
    override fun Result.reduce(event: ChannelsEvent): Any = when (event) {
        is ChannelsEvent.Internal.ItemsLoadingSuccess -> {
            if (event.items.isNotEmpty() || state.isSearchEnabled) {
                state { copy(status = ResourceStatus.SUCCESS, items = event.items) }
                effects { +ChannelsEffect.HideLoading }
            } else {
                Log.d("channels_reducer", "Channels list is empty")
            }
        }
        is ChannelsEvent.Internal.ChannelsLoadingSuccess -> {
            state {
                copy(
                    status = ResourceStatus.SUCCESS,
                    channels = event.channels,
                    subscribedChannels = event.subscribedChannels
                )
            }
            commands {
                +ChannelsCommand.LoadItems(
                    state.channels,
                    state.topics,
                    state.selectedChannelId
                )
            }
        }
        is ChannelsEvent.Internal.ChannelsSearchingSuccess -> {
            state { copy(status = ResourceStatus.SUCCESS, searchedChannels = event.channels) }
            commands {
                +ChannelsCommand.LoadItems(
                    state.searchedChannels,
                    state.topics,
                    state.selectedChannelId
                )
            }
            if (event.channels.isEmpty()) {
                effects { +ChannelsEffect.ShowEmpty }
            } else {
                effects { +ChannelsEffect.HideEmpty }
            }
        }
        is ChannelsEvent.Internal.TopicsLoadingSuccess -> {
            state { copy(status = ResourceStatus.SUCCESS, topics = event.topics) }
            if (state.isSearchEnabled) {
                commands {
                    +ChannelsCommand.LoadItems(
                        state.searchedChannels,
                        state.topics,
                        state.selectedChannelId
                    )
                }
            } else {
                commands {
                    +ChannelsCommand.LoadItems(
                        state.channels,
                        state.topics,
                        state.selectedChannelId
                    )
                }
            }
        }
        is ChannelsEvent.Internal.DataLoadingError -> {
            state { copy(status = ResourceStatus.ERROR) }
            effects { +ChannelsEffect.ShowError }
        }
        is ChannelsEvent.Ui.LoadChannels -> {
            state { copy(status = ResourceStatus.LOADING) }
            commands {
                +ChannelsCommand.LoadChannels(
                    state.channelsType,
                    state.subscribedChannels,
                    state.selectedChannelId
                )
            }
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
                    commands {
                        +ChannelsCommand.LoadItems(
                            state.channels,
                            state.topics,
                            state.selectedChannelId
                        )
                    }
                    effects { +ChannelsEffect.HideEmpty }
                }
            } else {
                Log.d("channels_reducer", "Search is unavailable while loading data")
            }
        }
        is ChannelsEvent.Ui.LoadTopics -> {
            state {
                copy(
                    status = ResourceStatus.LOADING,
                    topics = emptyList(),
                    selectedChannelId = event.channelId,
                    selectedChannelName = event.channelName
                )
            }
            commands {
                +ChannelsCommand.LoadTopics(
                    event.channelId,
                    state.channels,
                    state.selectedChannelId
                )
            }
        }
        is ChannelsEvent.Ui.HideTopics -> {
            state {
                copy(
                    status = ResourceStatus.LOADING,
                    topics = emptyList(),
                    selectedChannelId = -1,
                    selectedChannelName = ""
                )
            }
            if (state.isSearchEnabled) {
                commands {
                    +ChannelsCommand.LoadItems(
                        state.searchedChannels,
                        state.topics,
                        state.selectedChannelId
                    )
                }
            } else {
                commands {
                    +ChannelsCommand.LoadItems(
                        state.channels,
                        state.topics,
                        state.selectedChannelId
                    )
                }
            }
        }
        is ChannelsEvent.Ui.OnChannelClick -> {
            effects {
                +ChannelsEffect.OnChannelClicked(
                    event.channelId,
                    event.channelName,
                    event.isSelected
                )
            }
        }
        is ChannelsEvent.Ui.OnTopicClick -> {
            effects { +ChannelsEffect.OnTopicClicked(state.selectedChannelName, event.topicName) }
        }
    }
}