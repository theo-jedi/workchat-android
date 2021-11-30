package com.theost.workchat.elm.dialog

import com.theost.workchat.data.models.state.UpdateType
import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.data.repositories.ReactionsRepository
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class DialogActor(
    private val messagesRepository: MessagesRepository,
    private val reactionsRepository: ReactionsRepository
) : ActorCompat<DialogCommand, DialogEvent> {
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
            messagesRepository.getMessages(
                command.channelName,
                command.topicName,
                command.resourceType
            ).concatMap { messagesResult ->
                reactionsRepository.getReactions().map { reactionsResult ->
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
            messagesRepository.getMessagesFromServer(
                command.channelName,
                command.topicName,
                command.messages.last().id
            ).toObservable().concatMap { messagesResult ->
                reactionsRepository.getReactions().map { reactionsResult ->
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
            messagesRepository.getMessageFromServer(
                command.channelName,
                command.topicName,
                command.messageId
            ).toObservable().concatMap { messageResult ->
                reactionsRepository.getReactions().map { reactionsResult ->
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
            messagesRepository.addMessage(
                command.channelName,
                command.topicName,
                command.content
            ).mapEvents(DialogEvent.Internal.MessageSendingSuccess) { error ->
                DialogEvent.Internal.DataSendingError(error)
            }
        }
        is DialogCommand.AddReaction -> {
            reactionsRepository.addReaction(command.messageId, command.reactionName)
                .mapEvents(DialogEvent.Internal.ReactionSendingSuccess(command.messageId)) { error ->
                    DialogEvent.Internal.ReactionSendingError(error)
                }
        }
        is DialogCommand.RemoveReaction -> {
            reactionsRepository.removeReaction(
                command.messageId,
                command.reactionName,
                command.reactionCode,
                command.reactionType
            ).mapEvents(DialogEvent.Internal.ReactionSendingSuccess(command.messageId)) { error ->
                DialogEvent.Internal.ReactionSendingError(error)
            }
        }
    }
}