package com.theost.workchat.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NarrowDto(
    @SerialName("operator")
    val operator: String,
    @SerialName("operand")
    val operand: String
)