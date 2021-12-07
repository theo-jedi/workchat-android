package com.theost.workchat.elm.channels

import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.data.models.ui.ListChannel
import com.theost.workchat.data.models.ui.ListTopic
import com.theost.workchat.data.repositories.ChannelsRepository
import com.theost.workchat.data.repositories.TopicsRepository
import com.theost.workchat.utils.StringUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import vivid.money.elmslie.core.ActorCompat
import java.util.concurrent.TimeUnit


class ChannelsActor(
    private val channelsRepository: ChannelsRepository,
    private val topicsRepository: TopicsRepository
) : ActorCompat<ChannelsCommand, ChannelsEvent> {
    override fun execute(command: ChannelsCommand): Observable<ChannelsEvent> = when (command) {
        is ChannelsCommand.LoadItems -> {
            Observable.just(command.topics).concatMap { topics ->
                Observable.just(command.channels).map { channels ->
                    ChannelsItemsHelper.mapToListItems(channels, topics, command.selectedChannelId)
                }
            }.mapEvents(
                { items -> ChannelsEvent.Internal.ItemsLoadingSuccess(items) },
                { error -> ChannelsEvent.Internal.DataLoadingError(error) }
            )
        }
        is ChannelsCommand.LoadChannels -> {
            channelsRepository.getChannels(
                channelsType = command.channelsType,
                subscribedChannels = command.subscribedChannels
            ).map { result ->
                result.fold({ channels ->
                    ChannelsEvent.Internal.ChannelsLoadingSuccess(
                        channels.map { channel ->
                            ListChannel(
                                id = channel.id,
                                name = channel.name,
                                isSelected = channel.id == command.selectedChannelId
                            )
                        }, if (command.channelsType == ChannelsType.SUBSCRIBED) {
                            channels.map { channel -> channel.id }
                        } else {
                            emptyList()
                        }
                    )
                }, { error -> ChannelsEvent.Internal.DataLoadingError(error) })
            }
        }
        is ChannelsCommand.SearchChannels -> {
            Observable.just(command.channels)
                .distinctUntilChanged()
                .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
                .map { channels ->
                    channels.filter { channel ->
                        StringUtils.containsQuery(channel.name, command.query)
                    }
                }.mapEvents(
                    { channels -> ChannelsEvent.Internal.ChannelsSearchingSuccess(channels) },
                    { error -> ChannelsEvent.Internal.DataLoadingError(error) }
                )
        }
        is ChannelsCommand.LoadTopics -> {
            topicsRepository.getTopics(channelId = command.channelId).map { result ->
                result.fold({ topics ->
                    ChannelsEvent.Internal.TopicsLoadingSuccess(
                        topics.map { topic ->
                            ListTopic(
                                uid = topic.uid,
                                name = topic.name,
                                channelId = topic.channelId
                            )
                        }
                    )
                }, { error -> ChannelsEvent.Internal.DataLoadingError(error) })
            }
        }
    }
}