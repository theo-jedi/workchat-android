package com.theost.workchat.elm.creation.topic

sealed class CreationTopicCommand {
    data class CreateChannelTopic(
        val channelName: String,
        val channelDescription: String,
        val topicName: String,
        val topicMessage: String
    ) : CreationTopicCommand()
}