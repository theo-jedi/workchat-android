package com.theost.workchat.elm.dialog

import com.theost.workchat.data.models.state.MessageAction
import com.theost.workchat.data.models.state.UpdateType
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.ui.interfaces.DelegateItem

sealed class DialogEvent {
    sealed class Ui : DialogEvent() {
        object LoadMessages : Ui()
        object OnItemsInserted: Ui()

        data class LoadNextMessages(val savedPosition: Int) : Ui()

        data class OnMessageClicked(val messageId: Int) : Ui()

        data class OnReactionClicked(
            val actionType: MessageAction,
            val messageId: Int,
            val reactionName: String,
            val reactionCode: String,
            val reactionType: String
        ) : Ui()

        data class OnInputTextChanged(val text: String) : Ui()

        data class OnMessageActionClicked(val content: String) : Ui()
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

        data class ReactionSendingSuccess(val messageId: Int) : Internal()
        object MessageSendingSuccess : Internal()

        data class PaginationLoadingError(val error: Throwable) : Internal()
        data class ReactionSendingError(val error: Throwable) : Internal()
        data class DataLoadingError(val error: Throwable) : Internal()
        data class DataSendingError(val error: Throwable) : Internal()
    }
}