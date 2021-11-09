package com.theost.workchat.data.models.dto

import com.theost.workchat.data.models.core.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

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
data class GetUsersResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val message: String,
    @SerialName("members")
    val users: List<UserDto>
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
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("date_joined")
    val dateJoined: String,
    @SerialName("avatar_version")
    val avatarVersion: Int,
    @SerialName("role")
    val role: Int,
    @SerialName("is_owner")
    val isOwner: Boolean,
    @SerialName("is_guest")
    val isGuest: Boolean,
    @SerialName("is_bot")
    val isBot: Boolean,
    @SerialName("is_admin")
    val isAdmin: Boolean,
    @SerialName("is_billing_admin")
    val isBillingAdmin: Boolean,
    @SerialName("bot_type")
    val botType: Int? = null,
    @SerialName("bot_owner_id")
    val botOwnerId: Int? = null,
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
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("date_joined")
    val dateJoined: String,
    @SerialName("avatar_version")
    val avatarVersion: Int,
    @SerialName("role")
    val role: Int,
    @SerialName("is_owner")
    val isOwner: Boolean,
    @SerialName("is_guest")
    val isGuest: Boolean,
    @SerialName("is_bot")
    val isBot: Boolean,
    @SerialName("is_admin")
    val isAdmin: Boolean,
    @SerialName("is_billing_admin")
    val isBillingAdmin: Boolean,
    @SerialName("profile_data")
    val profileData: JsonObject,
    @SerialName("max_message_id")
    val maxMessageId: Int
)

fun UserDto.mapToUser(): User {
    return User(
        id = id,
        name = name,
        about = timezone,
        avatarUrl = avatarUrl,
        isActive = isActive,
        listOf()
    )
}

fun CurrentUserDto.mapToUser(): User {
    return User(
        id = id,
        name = name,
        about = timezone,
        avatarUrl = avatarUrl,
        isActive = isActive,
        listOf()
    )
}