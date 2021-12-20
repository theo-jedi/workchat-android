package com.theost.workchat.data.models.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("user_id")
    val id: Int,
    @SerialName("user_name")
    val name: String,
    @SerialName("user_email")
    val email: String,
    @SerialName("user_about")
    val about: String,
    @SerialName("avatar_url")
    val avatarUrl: String
)
