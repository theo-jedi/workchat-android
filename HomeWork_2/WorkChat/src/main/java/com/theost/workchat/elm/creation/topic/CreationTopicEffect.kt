package com.theost.workchat.elm.creation.topic

sealed class CreationTopicEffect {
    object ShowError : CreationTopicEffect()
    object ShowLoading : CreationTopicEffect()
    object HideLoading : CreationTopicEffect()
    object HideKeyboard : CreationTopicEffect()
    object EnableSubmitButton : CreationTopicEffect()
    object DisableSubmitButton : CreationTopicEffect()
    object OpenChannels : CreationTopicEffect()
}