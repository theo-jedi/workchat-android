package com.theost.workchat.elm.messenger

sealed class MessengerEffect {
    object HideNavigation : MessengerEffect()
    object ShowNavigation : MessengerEffect()
    object HideFloatingViews : MessengerEffect()
    object NavigateStreams : MessengerEffect()
    object NavigatePeople : MessengerEffect()
    object NavigateProfile : MessengerEffect()

    data class SelectNavigation(val itemId: Int) : MessengerEffect()
    data class OpenProfile(val userId: Int) : MessengerEffect()
    data class OpenDialog(val channelName: String, val topicName: String) : MessengerEffect()
}