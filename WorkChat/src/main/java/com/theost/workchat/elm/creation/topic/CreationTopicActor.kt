package com.theost.workchat.elm.creation.topic

import com.theost.workchat.data.repositories.MessagesRepository
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class CreationTopicActor(
    private val messagesRepository: MessagesRepository
) : ActorCompat<CreationTopicCommand, CreationTopicEvent> {
    override fun execute(command: CreationTopicCommand): Observable<CreationTopicEvent> =
        when (command) {
            is CreationTopicCommand.CreateChannelTopic -> {
                messagesRepository.addMessage(
                    command.channelName,
                    command.topicName,
                    command.topicMessage
                ).mapEvents(CreationTopicEvent.Internal.DataSendingSuccess) { error ->
                    CreationTopicEvent.Internal.DataSendingError(error)
                }
            }
        }
}