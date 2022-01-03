package com.theost.workchat.elm.creation.topic

import com.theost.workchat.data.models.state.ResourceStatus

data class CreationTopicState(
    val status: ResourceStatus = ResourceStatus.LOADING,
    val channelName: String = "",
    val channelDescription: String = "",
    val topicName: String = "",
    val topicMessage: String = ""
)