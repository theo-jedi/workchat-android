package com.theost.workchat.elm.dialog

import com.theost.workchat.data.models.state.DialogAction
import com.theost.workchat.data.models.state.MessageAction
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.data.models.state.UpdateType
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.ui.interfaces.DelegateItem

sealed class DialogEvent {
    sealed class Ui : DialogEvent() {
        object LoadMessages : Ui()
        object OnItemsInserted : Ui()
        object OnCloseEdit : Ui()
        object OnDownClicked : Ui()

        data class LoadNextMessages(val savedPosition: Int) : Ui()

        data class OnMessageClicked(
            val dialogAction: DialogAction,
            val messageType: MessageType,
            val messageId: Int,
            val content: String
        ) : Ui()

        data class OnMessageEditClicked(val content: String) : Ui()

        data class OnDialogActionClicked(
            val dialogAction: DialogAction,
            val messageId: Int,
            val content: String
        ) : Ui()

        data class OnReactionClicked(
            val actionType: MessageAction,
            val messageId: Int,
            val reactionName: String,
            val reactionCode: String,
            val reactionType: String
        ) : Ui()

        data class OnScrolled(val position: Int, val offset: Int) : Ui()
        data class OnInputTextChanged(val text: String) : Ui()
        data class OnMessageSendClicked(val content: String) : Ui()
        data class OnMessageCopy(val isCopied: Boolean) : Ui()
    }

    sealed class Internal : DialogEvent() {
        data class ItemsLoadingSuccess(
            val items: List<DelegateItem>,
            val messages: List<ListMessage>,
            val updateType: UpdateType
        ) : Internal()

        data class MessagesLoadingSuccess(
            val messages: List<ListMessage>,
            val updateType: UpdateType
        ) : Internal()

        object MessageSendingSuccess : Internal()

        data class MessageEditingSuccess(val messageId: Int) : Internal()
        data class MessageEditingError(val error: Throwable) : Internal()
        data class MessageDeletionSuccess(val messageId: Int) : Internal()
        data class MessageDeletionError(val error: Throwable) : Internal()
        data class ReactionSendingSuccess(val messageId: Int) : Internal()
        data class PaginationLoadingError(val error: Throwable) : Internal()
        data class ReactionSendingError(val error: Throwable) : Internal()
        data class DataLoadingError(val error: Throwable) : Internal()
        data class DataSendingError(val error: Throwable) : Internal()
    }
}