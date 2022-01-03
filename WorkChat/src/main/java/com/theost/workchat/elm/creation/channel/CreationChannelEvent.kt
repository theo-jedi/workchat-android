package com.theost.workchat.elm.creation.channel

sealed class CreationChannelEvent {
    sealed class Ui : CreationChannelEvent() {
        object LoadChannels : Ui()
        object OnStateRestored : Ui()
        data class OnInputTextChanged(val text: String) : Ui()
        data class OnSubmitClicked(val channelName: String, val channelDescription: String) : Ui()
    }

    sealed class Internal : CreationChannelEvent() {
        object DataSendingSuccess: Internal()
        data class DataLoadingSuccess(val channels: List<String>) : Internal()
        data class DataLoadingError(val error: Throwable) : Internal()
        data class DataSendingError(val error: Throwable) : Internal()
    }
}