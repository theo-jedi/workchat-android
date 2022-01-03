package com.theost.workchat.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddPhotoResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String,
    @SerialName("uri")
    val uri: String
)

fun AddPhotoResponse.getUri(): String {
    return uri
}