package com.theost.workchat.elm.creation.channel

sealed class CreationChannelEffect {
    object ShowSendingLoading : CreationChannelEffect()
    object HideSendingLoading : CreationChannelEffect()
    object ShowLoadingError : CreationChannelEffect()
    object ShowSendingError : CreationChannelEffect()
    object SwitchChannelCreation : CreationChannelEffect()
    object SwitchTopicCreation : CreationChannelEffect()
    object HideKeyboard : CreationChannelEffect()
    object EnableSubmitButton : CreationChannelEffect()
    object DisableSubmitButton : CreationChannelEffect()
    object OpenChannels : CreationChannelEffect()

    data class OpenCreationTopic(
        val channelName: String,
        val channelDescription: String
    ) : CreationChannelEffect()
}