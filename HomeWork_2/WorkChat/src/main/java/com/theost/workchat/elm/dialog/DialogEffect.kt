package com.theost.workchat.elm.dialog

import com.theost.workchat.data.models.state.MessageType

sealed class DialogEffect {
    object ShowLoadingError : DialogEffect()
    object ShowPaginationError : DialogEffect()
    object ShowSendingError : DialogEffect()
    object ShowSendingMessageLoading : DialogEffect()
    object HideSendingMessageLoading : DialogEffect()
    object ShowEditingMessageLoading : DialogEffect()
    object HideEditingMessageLoading : DialogEffect()
    object ClearSendingMessageContent : DialogEffect()
    object ShowDialogLoading : DialogEffect()
    object HideDialogLoading : DialogEffect()
    object ShowEmpty : DialogEffect()
    object HideEmpty : DialogEffect()
    object ShowSendMessageAction : DialogEffect()
    object ShowAttachMessageAction : DialogEffect()
    object ShowDownButton : DialogEffect()
    object HideDownButton : DialogEffect()
    object ScrollSmoothToBottom : DialogEffect()
    object ScrollToBottom : DialogEffect()
    object ShowCopySuccess : DialogEffect()
    object ShowCopyError : DialogEffect()
    object HideMessageEdit : DialogEffect()

    data class ShowActionsPicker(
        val messageType: MessageType,
        val messageId: Int,
        val content: String
    ) : DialogEffect()

    data class ScrollToTop(val position: Int) : DialogEffect()
    data class ShowTitle(val channel: String, val topic: String) : DialogEffect()
    data class ShowReactionPicker(val messageId: Int) : DialogEffect()
    data class ShowMessageEdit(val content: String) : DialogEffect()
    data class CopyMessage(val content: String) : DialogEffect()
}
