package com.theost.workchat.ui.interfaces

interface TopicListener {
    fun openDialog(channelName: String, topicName: String)
    fun openChannels()
    fun createChannel()
    fun createTopic(channelName: String, description: String)
}