package com.theost.workchat.elm.dialog

import android.util.Log
import com.theost.workchat.data.models.state.*
import com.theost.workchat.data.models.ui.ListLoader
import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.utils.ApiUtils
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer
import kotlin.math.absoluteValue

class DialogReducer : DslReducer<DialogEvent, DialogState, DialogEffect, DialogCommand>() {
    override fun Result.reduce(event: DialogEvent): Any = when (event) {
        is DialogEvent.Internal.ItemsLoadingSuccess -> {
            val isFirstLoad = state.items.isEmpty()
            state {
                copy(
                    status = ResourceStatus.SUCCESS,
                    items = event.items,
                    messages = event.messages
                )
            }
            if (!isFirstLoad && event.items.isEmpty()) {
                effects { +DialogEffect.ShowEmpty }
            }
            when (event.updateType) {
                UpdateType.INITIAL -> {
                    if (isFirstLoad) state { copy(scrollStatus = ScrollStatus.SCROLL_BOTTOM) }
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
                    if (state.editedMessageId != -1) {
                        state { copy(editedMessageId = -1, editedMessageContent = "") }
                        effects { +DialogEffect.HideEditingMessageLoading }
                        effects { +DialogEffect.HideMessageEdit }
                        effects { +DialogEffect.ClearSendingMessageContent }
                    } else {
                        Log.d("dialog_reducer", "Reaction updated")
                    }
                }
                UpdateType.DELETE -> {
                    state { copy(scrollStatus = ScrollStatus.STAY) }
                }
            }
        }
        is DialogEvent.Internal.MessagesLoadingSuccess -> {
            if (event.updateType == UpdateType.INITIAL && event.messages.isEmpty()) {
                commands { +DialogCommand.LoadItems(event.messages, event.updateType) }
                effects { +DialogEffect.HideDialogLoading }
                effects { +DialogEffect.ShowEmpty }
            } else {
                if (event.updateType == UpdateType.PAGINATION) {
                    if (state.messages.isNotEmpty() && event.messages.last().id == state.messages.last().id) {
                        state { copy(paginationStatus = PaginationStatus.FULLY) }
                    } else {
                        state { copy(paginationStatus = PaginationStatus.PARTIAL) }
                    }
                }
                effects { +DialogEffect.HideEmpty }
            }
            if (ApiUtils.containsEmptyMessage(event.messages)) {
                if (event.messages.size > 1) {
                    commands {
                        +DialogCommand.LoadItems(
                            ApiUtils.removeEmptyMessage(event.messages),
                            event.updateType
                        )
                    }
                } else {
                    Log.d("dialog_reducer", "Messages list is empty")
                }
            } else {
                commands { +DialogCommand.LoadItems(event.messages, event.updateType) }
            }
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
        is DialogEvent.Internal.MessageEditingSuccess -> {
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
        is DialogEvent.Internal.MessageDeletionSuccess -> {
            state { copy(messages = messages.filterNot { message -> message.id == event.messageId }) }
            commands { +DialogCommand.LoadItems(state.messages, UpdateType.DELETE) }
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
            effects { +DialogEffect.HideSendingMessageLoading }
        }
        is DialogEvent.Internal.MessageEditingError -> {
            state { copy(status = ResourceStatus.ERROR) }
            effects { +DialogEffect.ShowSendingError }
            effects { +DialogEffect.HideEditingMessageLoading }
        }
        is DialogEvent.Internal.MessageDeletionError -> {
            state { copy(status = ResourceStatus.ERROR) }
            effects { +DialogEffect.ShowSendingError }
        }
        is DialogEvent.Internal.PaginationLoadingError -> {
            state {
                copy(
                    status = ResourceStatus.ERROR,
                    items = items.filterNot { it is ListLoader })
            }
            effects { +DialogEffect.ShowPaginationError }
        }
        is DialogEvent.Internal.DataLoadingError -> {
            state { copy(status = ResourceStatus.ERROR) }
            if (state.items.isNotEmpty()) effects { +DialogEffect.HideDialogLoading }
            effects { +DialogEffect.ShowLoadingError }
        }
        is DialogEvent.Internal.ReactionSendingError -> {
            Log.d("dialog_reducer", "Reaction sending error")
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
            if (state.paginationStatus != PaginationStatus.FULLY
                && state.status != ResourceStatus.LOADING
                && event.savedPosition != state.savedPosition
                && state.isScrolled
            ) {
                if (state.items.count() >= MessagesRepository.DIALOG_PAGE_SIZE) {
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
                    state { copy(paginationStatus = PaginationStatus.FULLY) }
                }
            } else {
                Log.d("dialog_reducer", "Dialog is loading or fully loaded")
            }
            Log.d("dialog_reducer", "Dialog is loading or fully loaded")
        }
        is DialogEvent.Ui.OnItemsInserted -> {
            when (state.scrollStatus) {
                ScrollStatus.SCROLL_BOTTOM -> {
                    state { copy(scrollStatus = ScrollStatus.STAY) }
                    effects { +DialogEffect.ScrollToBottom }
                }
                ScrollStatus.SCROLL_TOP -> {
                    state { copy(scrollStatus = ScrollStatus.STAY) }
                    effects { +DialogEffect.ScrollToTop(state.savedPosition) }
                }
                ScrollStatus.STAY -> {
                    Log.d("dialog_reducer", "ScrollStatus - STAY")
                }
            }
        }
        is DialogEvent.Ui.OnMessageSendClicked -> {
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
        is DialogEvent.Ui.OnMessageEditClicked -> {
            if (event.content.isNotEmpty() && event.content != state.editedMessageContent) {
                state { copy(status = ResourceStatus.LOADING) }
                commands { +DialogCommand.EditMessage(state.editedMessageId, event.content) }
                effects { +DialogEffect.ShowEditingMessageLoading }
            } else {
                state { copy(editedMessageId = -1, editedMessageContent = "") }
                effects { +DialogEffect.HideMessageEdit }
                effects { +DialogEffect.ClearSendingMessageContent }
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
        is DialogEvent.Ui.OnDownClicked -> {
            state { copy(downStatus = DownStatus.SCROLLING) }
            effects { +DialogEffect.HideDownButton }
            effects { +DialogEffect.ScrollSmoothToBottom }
        }
        is DialogEvent.Ui.OnScrolled -> {
            if (event.position == 0) state { copy(isScrolled = true) }
            if (event.position <= DOWN_BUTTON_POSITION) {
                state { copy(downStatus = DownStatus.HIDDEN, scrollOffset = 0) }
                if (event.position >= state.items.count() - MessagesRepository.DIALOG_PAGE_SIZE) {
                    commands {
                        +DialogCommand.LoadNextMessages(
                            state.channelName,
                            state.topicName,
                            state.currentUserId,
                            state.messages
                        )
                    }
                }
                effects { +DialogEffect.HideDownButton }
            } else if ((state.scrollOffset + event.offset).absoluteValue > DOWN_BUTTON_SCROLL) {
                if (state.scrollOffset + event.offset > 0) {
                    if (state.downStatus != DownStatus.SCROLLING) {
                        state { copy(downStatus = DownStatus.VISIBLE, scrollOffset = 0) }
                        effects { +DialogEffect.ShowDownButton }
                    } else {
                        { /* do nothing */ }
                    }
                } else {
                    state { copy(downStatus = DownStatus.HIDDEN, scrollOffset = 0) }
                    effects { +DialogEffect.HideDownButton }
                }
            } else {
                state { copy(scrollOffset = state.scrollOffset + event.offset) }
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
            when (event.dialogAction) {
                DialogAction.SHOW_ACTIONS_PICKER -> {
                    effects {
                        +DialogEffect.ShowActionsPicker(
                            event.messageType,
                            event.messageId,
                            event.content
                        )
                    }
                }
                DialogAction.SHOW_REACTION_PICKER -> {
                    effects { +DialogEffect.ShowReactionPicker(event.messageId) }
                }
                else -> {
                    Log.d("dialog_reducer", "Message action is not allowed")
                }
            }
        }
        is DialogEvent.Ui.OnDialogActionClicked -> {
            when (event.dialogAction) {
                DialogAction.SHOW_REACTION_PICKER -> {
                    effects { +DialogEffect.ShowReactionPicker(event.messageId) }
                }
                DialogAction.COPY_MESSAGE -> {
                    effects { +DialogEffect.CopyMessage(event.content) }
                }
                DialogAction.EDIT_MESSAGE -> {
                    state {
                        copy(
                            editedMessageId = event.messageId,
                            editedMessageContent = event.content
                        )
                    }
                    effects { +DialogEffect.ShowMessageEdit(event.content) }
                }
                DialogAction.DELETE_MESSAGE -> {
                    state { copy(status = ResourceStatus.LOADING) }
                    commands { +DialogCommand.DeleteMessage(event.messageId) }
                }
                DialogAction.SHOW_ACTIONS_PICKER -> {
                    /* already opened */
                }
            }
        }
        is DialogEvent.Ui.OnMessageCopy -> {
            if (event.isCopied) {
                effects { +DialogEffect.ShowCopySuccess }
            } else {
                effects { +DialogEffect.ShowCopyError }
            }
        }
        is DialogEvent.Ui.OnCloseEdit -> {
            state { copy(editedMessageId = -1, editedMessageContent = "") }
            effects { +DialogEffect.HideMessageEdit }
            effects { +DialogEffect.ClearSendingMessageContent }
        }
    }

    companion object {
        const val DOWN_BUTTON_SCROLL = 100
        const val DOWN_BUTTON_POSITION = 2
    }
}