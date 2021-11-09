package com.theost.workchat.data.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageBody(
    @SerialName("to")
    val stream: String,
    @SerialName("topic")
    val topic: String,
    @SerialName("content")
    val content: String,
    @SerialName("type")
    val type: String
)