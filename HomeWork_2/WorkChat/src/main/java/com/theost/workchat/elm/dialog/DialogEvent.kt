package com.theost.workchat.elm.dialog

import com.theost.workchat.data.models.state.*
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.ui.interfaces.DelegateItem
import java.io.File

sealed class DialogEvent {
    sealed class Ui : DialogEvent() {
        object LoadMessages : Ui()
        object OnItemsInserted : Ui()
        object OnCloseEdit : Ui()
        object OnDownClicked : Ui()
        object OnPhotoCopyingFileError : Ui()
        object OnPhotoCopyingSizeError : Ui()

        data class LoadNextMessages(val savedPosition: Int) : Ui()

        data class OnMessageClicked(
            val dialogAction: DialogAction,
            val messageType: MessageType,
            val contentType: ContentType,
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
        data class OnPhotoSend(val file: File) : Ui()
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

        data class PhotoSendingSuccess(val uri: String) : Internal()
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