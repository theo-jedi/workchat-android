package com.theost.workchat.elm.creation.channel

import com.theost.workchat.data.models.state.CreationStatus
import com.theost.workchat.data.models.state.ResourceStatus

data class CreationChannelState(
    val status: ResourceStatus = ResourceStatus.LOADING,
    val stateStatus: ResourceStatus = ResourceStatus.LOADING,
    val creationStatus: CreationStatus = CreationStatus.TOPIC,
    val channels: List<String> = emptyList(),
    val channelName: String = "",
    val channelDescription: String = ""
)