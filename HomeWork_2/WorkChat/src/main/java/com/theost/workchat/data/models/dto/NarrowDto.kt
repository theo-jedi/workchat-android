package com.theost.workchat.data.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NarrowDto(
    @SerialName("operator")
    val operator: String,
    @SerialName("operand")
    val operand: String
)