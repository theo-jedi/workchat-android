package com.theost.workchat.elm.profile

sealed class ProfileEffect {
    object ShowError : ProfileEffect()
    object ShowLoading : ProfileEffect()
    object HideLoading : ProfileEffect()
}