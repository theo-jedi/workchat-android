package com.theost.workchat.elm.dialog

import android.util.Log
import com.theost.workchat.data.models.state.*
import com.theost.workchat.data.models.ui.ListLoader
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class DialogReducer : DslReducer<DialogEvent, DialogState, DialogEffect, DialogCommand>() {
    override fun Result.reduce(event: DialogEvent): Any = when (event) {
        is DialogEvent.Internal.ItemsLoadingSuccess -> {
            state {
                copy(
                    status = ResourceStatus.SUCCESS,
                    items = event.items,
                    messages = event.messages
                )
            }
            if (event.items.isNotEmpty()) {
                when (event.updateType) {
                    UpdateType.INITIAL -> {
                        state { copy(scrollStatus = ScrollStatus.SCROLL_BOTTOM) }
                        effects { +DialogEffect.HideDialogLoading }
                    }
                    UpdateType.RELOAD -> {
                        state { copy(scrollStatus = ScrollStatus.SCROLL_BOTTOM) }
                        effects { +DialogEffect.ScrollToBottom }
                        effects { +DialogEffect.HideSendingMessageLoading }
                    }
                    UpdateType.PAGINATION -> {
                        state { copy(scrollStatus = ScrollStatus.SCROLL_TOP) }
                    }
                    UpdateType.UPDATE -> {
                        /* do nothing */
                    }
                }
            } else {
                Log.d("dialog_reducer", "Messages list is empty")
            }
        }
        is DialogEvent.Internal.MessagesLoadingSuccess -> {
            if (event.updateType == UpdateType.PAGINATION) {
                if (state.messages.isNotEmpty() && event.messages.last().id == state.messages.last().id) {
                    state { copy(paginationStatus = PaginationStatus.FULLY) }
                } else {
                    state { copy(paginationStatus = PaginationStatus.PARTIAL) }
                }
            }
            commands { +DialogCommand.LoadItems(event.messages, event.updateType) }
        }
        is DialogEvent.Internal.MessageSendingSuccess -> {
            state { copy(savedPosition = 0) }
            commands {
                +DialogCommand.LoadMessages(
                    state.channelName,
                    state.topicName,
                    state.currentUserId,
                    ResourceType.SERVER,
                    UpdateType.RELOAD
                )
            }
            effects { +DialogEffect.ClearSendingMessageContent }
        }
        is DialogEvent.Internal.ReactionSendingSuccess -> {
            commands {
                +DialogCommand.LoadMessage(
                    state.channelName,
                    state.topicName,
                    state.currentUserId,
                    state.messages,
                    event.messageId
                )
            }
        }
        is DialogEvent.Internal.DataSendingError -> {
            state { copy(status = ResourceStatus.ERROR) }
            effects { +DialogEffect.ShowSendingError }
        }
        is DialogEvent.Internal.PaginationLoadingError -> {
            state {
                copy(
                    status = ResourceStatus.ERROR,
                    items = items.filterNot { it is ListLoader })
            }
            effects { +DialogEffect.ShowLoadingError }
        }
        is DialogEvent.Internal.DataLoadingError -> {
            state { copy(status = ResourceStatus.ERROR) }
            effects { +DialogEffect.ShowLoadingError }
        }
        is DialogEvent.Internal.ReactionSendingError -> {
            /* do nothing */
        }
        is DialogEvent.Ui.LoadMessages -> {
            state { copy(status = ResourceStatus.LOADING) }
            commands {
                +DialogCommand.LoadMessages(
                    state.channelName,
                    state.topicName,
                    state.currentUserId,
                    ResourceType.CACHE_AND_SERVER,
                    UpdateType.INITIAL
                )
            }
            effects { +DialogEffect.ShowDialogLoading }
            effects { +DialogEffect.ShowTitle(state.channelName, state.topicName) }
        }
        is DialogEvent.Ui.LoadNextMessages -> {
            if (state.status != ResourceStatus.LOADING && state.paginationStatus != PaginationStatus.FULLY) {
                state {
                    copy(
                        status = ResourceStatus.LOADING,
                        scrollStatus = ScrollStatus.STAY,
                        items = items + listOf(ListLoader()),
                        savedPosition = event.savedPosition
                    )
                }
                commands {
                    +DialogCommand.LoadNextMessages(
                        state.channelName,
                        state.topicName,
                        state.currentUserId,
                        state.messages
                    )
                }
            } else {
                Log.d("dialog_reducer", "Dialog is loading or fully loaded")
            }
        }
        is DialogEvent.Ui.OnItemsInserted -> {
            when (state.scrollStatus) {
                ScrollStatus.SCROLL_BOTTOM -> effects { +DialogEffect.ScrollToBottom }
                ScrollStatus.SCROLL_TOP -> effects { +DialogEffect.ScrollToTop(state.savedPosition) }
                ScrollStatus.STAY -> {
                    /* do nothing */
                }
            }
        }
        is DialogEvent.Ui.OnMessageActionClicked -> {
            if (state.inputStatus == InputStatus.FILLED) {
                state { copy(status = ResourceStatus.LOADING) }
                commands {
                    +DialogCommand.AddMessage(
                        state.channelName,
                        state.topicName,
                        event.content
                    )
                }
                effects { +DialogEffect.ShowSendingMessageLoading }
            } else {
                { /* file attachment */ }
            }
        }
        is DialogEvent.Ui.OnReactionClicked -> {
            state { copy(status = ResourceStatus.LOADING) }
            when (event.actionType) {
                MessageAction.REACTION_ADD -> {
                    commands { +DialogCommand.AddReaction(event.messageId, event.reactionName) }
                }
                MessageAction.REACTION_REMOVE -> {
                    commands {
                        +DialogCommand.RemoveReaction(
                            event.messageId,
                            event.reactionName,
                            event.reactionCode,
                            event.reactionType
                        )
                    }
                }
            }
        }
        is DialogEvent.Ui.OnInputTextChanged -> {
            if (event.text.isEmpty()) {
                if (state.inputStatus == InputStatus.FILLED) {
                    state { copy(inputStatus = InputStatus.EMPTY) }
                    effects { +DialogEffect.ShowAttachMessageAction }
                } else {
                    { /* do nothing */ }
                }
            } else {
                if (state.inputStatus == InputStatus.EMPTY) {
                    state { copy(inputStatus = InputStatus.FILLED) }
                    effects { +DialogEffect.ShowSendMessageAction }
                } else {
                    { /* do nothing */ }
                }
            }
        }
        is DialogEvent.Ui.OnMessageClicked -> {
            effects { +DialogEffect.ShowReactionPicker(event.messageId) }
        }
    }
}