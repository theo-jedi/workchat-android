package com.theost.workchat.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamDto(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String
)