package com.theost.workchat.elm.creation.channel

import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.data.repositories.ChannelsRepository
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class CreationChannelActor(
    private val channelsRepository: ChannelsRepository
) : ActorCompat<CreationChannelCommand, CreationChannelEvent> {
    override fun execute(command: CreationChannelCommand): Observable<CreationChannelEvent> =
        when (command) {
            is CreationChannelCommand.LoadChannels -> {
                channelsRepository.getChannelsFromCache(ChannelsType.ALL).toObservable()
                    .map { channelsResult ->
                        channelsResult.fold({ channels ->
                            CreationChannelEvent.Internal.DataLoadingSuccess(channels.map { channel ->
                                channel.name
                            })
                        }, { error -> CreationChannelEvent.Internal.DataLoadingError(error) })
                    }
            }
            is CreationChannelCommand.CreateChannel -> {
                channelsRepository.addChannel(command.channelName, command.channelDescription)
                    .toObservable().map { result ->
                        result.fold(
                            { CreationChannelEvent.Internal.DataSendingSuccess },
                            { error -> CreationChannelEvent.Internal.DataSendingError(error) }
                        )
                    }
            }
        }
}