package com.theost.workchat.elm.messenger

sealed class MessengerCommand {
    object LoadUser : MessengerCommand()
}