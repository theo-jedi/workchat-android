package com.theost.workchat.elm.profile

sealed class ProfileCommand {
    data class LoadProfile(val userId: Int) : ProfileCommand()
}
