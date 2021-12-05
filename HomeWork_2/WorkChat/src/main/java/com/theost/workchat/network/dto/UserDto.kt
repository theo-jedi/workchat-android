package com.theost.workchat.network.dto

import com.theost.workchat.data.models.core.User
import com.theost.workchat.data.models.state.UserStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUsersResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String,
    @SerialName("members")
    val users: List<UserDto>
)

@Serializable
data class GetUserResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String,
    @SerialName("user")
    val user: UserDto
)

@Serializable
data class GetUserPresenceResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String,
    @SerialName("presence")
    val presence: UserPresenceDto
)

@Serializable
data class UserDto(
    @SerialName("user_id")
    val id: Int,
    @SerialName("full_name")
    val name: String,
    @SerialName("email")
    val email: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("is_bot")
    val isBot: Boolean
)

@Serializable
data class CurrentUserDto(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String,
    @SerialName("user_id")
    val id: Int,
    @SerialName("full_name")
    val name: String,
    @SerialName("email")
    val email: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("timezone")
    val timezone: String
)

@Serializable
data class UserPresenceDto(
    @SerialName("aggregated")
    val client: UserPresenceClientDto
)

@Serializable
data class UserPresenceClientDto(
    @SerialName("status")
    val status: String?
)

fun UserDto.mapToUser(): User {
    return User(
        id = id,
        name = name,
        email = email,
        about = timezone,
        avatarUrl = avatarUrl,
        isBot = isBot
    )
}

fun CurrentUserDto.mapToUser(): User {
    return User(
        id = id,
        name = name,
        email = email,
        about = timezone,
        avatarUrl = avatarUrl,
        isBot = false
    )
}

fun UserPresenceClientDto.mapToStatus() : UserStatus {
    return when (status) {
        UserStatus.ONLINE.apiName -> UserStatus.ONLINE
        UserStatus.IDLE.apiName -> UserStatus.IDLE
        else -> UserStatus.OFFLINE
    }
}