package com.theost.workchat.elm.dialog

sealed class DialogEffect {
    object ShowLoadingError : DialogEffect()
    object ShowSendingError : DialogEffect()
    object ShowSendingMessageLoading : DialogEffect()
    object HideSendingMessageLoading : DialogEffect()
    object ClearSendingMessageContent : DialogEffect()
    object ShowDialogLoading : DialogEffect()
    object HideDialogLoading : DialogEffect()
    object ShowEmpty : DialogEffect()
    object HideEmpty : DialogEffect()
    object ShowSendMessageAction : DialogEffect()
    object ShowAttachMessageAction : DialogEffect()
    object ScrollToBottom : DialogEffect()

    data class ScrollToTop(val position: Int) : DialogEffect()
    data class ShowTitle(val channel: String, val topic: String) : DialogEffect()
    data class ShowReactionPicker(val messageId: Int) : DialogEffect()
}
