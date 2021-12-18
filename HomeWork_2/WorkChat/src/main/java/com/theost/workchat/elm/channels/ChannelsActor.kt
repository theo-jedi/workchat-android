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
                    var topicPosition = -1
                    val listTopics = mutableListOf<ListTopic>().apply {
                        topics.forEachIndexed { index, topic ->
                            if (index == 0 || topics[index].channelId == topics[index - 1].channelId) {
                                topicPosition += 1
                            } else {
                                topicPosition = 0
                            }
                            add(
                                ListTopic(
                                    uid = topic.uid,
                                    name = topic.name,
                                    channelId = topic.channelId,
                                    position = topicPosition
                                )
                            )
                        }
                    }.toList()
                    ChannelsEvent.Internal.TopicsLoadingSuccess(listTopics)
                }, { error -> ChannelsEvent.Internal.DataLoadingError(error) })
            }
        }
    }
}