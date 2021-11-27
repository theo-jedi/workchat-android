package com.theost.workchat.elm.dialog

import com.theost.workchat.data.models.state.UpdateType
import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.data.repositories.ReactionsRepository
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class DialogActor : ActorCompat<DialogCommand, DialogEvent> {
    override fun execute(command: DialogCommand): Observable<DialogEvent> = when (command) {
        is DialogCommand.LoadItems -> {
            Observable.just(command.messages).map { messages ->
                Pair(DialogItemHelper.mapToListItems(messages), command.messages)
            }.mapEvents(
                { items ->
                    DialogEvent.Internal.ItemsLoadingSuccess(
                        items.first,
                        items.second,
                        command.updateType
                    )
                },
                { error -> DialogEvent.Internal.DataLoadingError(error) }
            )
        }
        is DialogCommand.LoadMessages -> {
            MessagesRepository.getMessages(
                command.channelName,
                command.topicName,
                command.resourceType
            ).concatMap { messagesResult ->
                ReactionsRepository.getReactions().map { reactionsResult ->
                    messagesResult.fold({ messages ->
                        reactionsResult.fold({ reactions ->
                            DialogEvent.Internal.MessagesLoadingSuccess(
                                DialogItemHelper.mapToListMessages(
                                    messages,
                                    reactions,
                                    command.currentUserId
                                ), command.updateType
                            )
                        }, { error -> DialogEvent.Internal.DataLoadingError(error) })
                    }, { error -> DialogEvent.Internal.DataLoadingError(error) })
                }
            }
        }
        is DialogCommand.LoadNextMessages -> {
            MessagesRepository.getMessagesFromServer(
                command.channelName,
                command.topicName,
                command.messages.last().id
            ).toObservable().concatMap { messagesResult ->
                ReactionsRepository.getReactions().map { reactionsResult ->
                    messagesResult.fold({ messages ->
                        reactionsResult.fold({ reactions ->
                            DialogEvent.Internal.MessagesLoadingSuccess(
                                DialogItemHelper.mergeMessages(
                                    command.messages,
                                    messages,
                                    reactions,
                                    command.currentUserId
                                ), UpdateType.PAGINATION
                            )
                        }, { error -> DialogEvent.Internal.PaginationLoadingError(error) })
                    }, { error -> DialogEvent.Internal.PaginationLoadingError(error) })
                }
            }
        }
        is DialogCommand.LoadMessage -> {
            MessagesRepository.getMessageFromServer(
                command.channelName,
                command.topicName,
                command.messageId
            ).toObservable().concatMap { messageResult ->
                ReactionsRepository.getReactions().map { reactionsResult ->
                    messageResult.fold({ message ->
                        reactionsResult.fold({ reactions ->
                            DialogEvent.Internal.MessagesLoadingSuccess(
                                DialogItemHelper.replaceMessage(
                                    command.messages,
                                    message,
                                    reactions,
                                    command.currentUserId
                                ), UpdateType.UPDATE
                            )
                        }, { error -> DialogEvent.Internal.DataLoadingError(error) })
                    }, { error -> DialogEvent.Internal.DataLoadingError(error) })
                }
            }
        }
        is DialogCommand.AddMessage -> {
            MessagesRepository.addMessage(
                command.channelName,
                command.topicName,
                command.content
            ).mapEvents(DialogEvent.Internal.MessageSendingSuccess) { error ->
                DialogEvent.Internal.DataSendingError(error)
            }
        }
        is DialogCommand.AddReaction -> {
            ReactionsRepository.addReaction(command.messageId, command.reactionName)
                .mapEvents(DialogEvent.Internal.ReactionSendingSuccess(command.messageId)) { error ->
                    DialogEvent.Internal.DataSendingError(error)
                }
        }
        is DialogCommand.RemoveReaction -> {
            ReactionsRepository.removeReaction(
                command.messageId,
                command.reactionName,
                command.reactionCode,
                command.reactionType
            ).mapEvents(DialogEvent.Internal.ReactionSendingSuccess(command.messageId)) { error ->
                DialogEvent.Internal.DataSendingError(error)
            }
        }
    }
}