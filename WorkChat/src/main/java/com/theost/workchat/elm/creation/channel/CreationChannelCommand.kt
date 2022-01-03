package com.theost.workchat.elm.creation.channel

sealed class CreationChannelCommand {
    object LoadChannels : CreationChannelCommand()

    data class CreateChannel(
        val channelName: String,
        val channelDescription: String
    ) : CreationChannelCommand()
}