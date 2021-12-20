package com.theost.workchat.data.models.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reaction(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("code")
    val code: String,
    @SerialName("type")
    val type: String,
    @SerialName("emoji")
    val emoji: String
)
