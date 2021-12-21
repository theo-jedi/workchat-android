package com.theost.workchat.elm.messenger

sealed class MessengerEvent {

    sealed class Ui : MessengerEvent() {
        object OnBackPress : MessengerEvent()
        data class OnNavigationClick(val itemId: Int) : MessengerEvent()
        data class OnProfileClick(val userId: Int) : MessengerEvent()
        data class OnDialogClick(val channelName: String, val topicName: String) : MessengerEvent()
        data class Init(val itemId: Int) : MessengerEvent()
    }

    sealed class Internal : MessengerEvent() {
        data class DataLoadingSuccess(val userId: Int) : Internal()
        data class DataLoadingError(val error: Throwable) : Internal()
    }

}