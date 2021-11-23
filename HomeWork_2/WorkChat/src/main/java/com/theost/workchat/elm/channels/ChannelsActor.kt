package com.theost.workchat.elm.channels

import com.theost.workchat.data.models.ui.ListChannel
import com.theost.workchat.data.models.ui.ListTopic
import com.theost.workchat.data.repositories.ChannelsRepository
import com.theost.workchat.data.repositories.TopicsRepository
import com.theost.workchat.utils.StringUtils
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import vivid.money.elmslie.core.ActorCompat
import java.util.concurrent.TimeUnit


class ChannelsActor : ActorCompat<ChannelsCommand, ChannelsEvent> {
    override fun execute(command: ChannelsCommand): Observable<ChannelsEvent> = when (command) {
        is ChannelsCommand.LoadChannels -> {
            ChannelsRepository.getChannels(
                channelsType = command.channelsType,
                subscribedChannels = command.subscribedChannels
            ).map { list ->
                list.map { channel ->
                    ListChannel(
                        id = channel.id,
                        name = channel.name,
                        isSelected = false
                    )
                }
            }.mapEvents(
                { channels -> ChannelsEvent.Internal.ChannelsLoadingSuccess(channels) },
                { error -> ChannelsEvent.Internal.DataLoadingError(error) }
            )
        }
        is ChannelsCommand.SearchChannels -> {
            Observable.just(command.channels)
                .distinctUntilChanged()
                .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
                .map { list ->
                    list.filter { channel ->
                        StringUtils.containsQuery(channel.name, command.query)
                    }
                }.mapEvents(
                    { channels -> ChannelsEvent.Internal.ChannelsSearchingSuccess(channels) },
                    { error -> ChannelsEvent.Internal.DataLoadingError(error) }
                )
        }
        is ChannelsCommand.LoadTopics -> {
            TopicsRepository.getTopics(
                channelId = command.channelId
            ).map { list ->
                list.map { topic ->
                    ListTopic(
                        name = topic.name,
                        lastMessageId = topic.lastMessageId
                    )
                }
            }.mapEvents(
                { topics -> ChannelsEvent.Internal.TopicsLoadingSuccess(topics) },
                { error -> ChannelsEvent.Internal.DataLoadingError(error) }
            )
        }
        is ChannelsCommand.RestoreChannels -> {
            Single.just(command.channels).mapEvents(
                { channels -> ChannelsEvent.Internal.ChannelsLoadingSuccess(channels) },
                { error -> ChannelsEvent.Internal.DataLoadingError(error) }
            )
        }
    }
}