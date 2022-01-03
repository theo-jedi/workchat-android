package com.theost.workchat.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theost.workchat.data.models.core.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val id: Int,
    @ColumnInfo(name = "user_name")
    val name: String,
    @ColumnInfo(name = "user_email")
    val email: String,
    @ColumnInfo(name = "user_about")
    val about: String,
    @ColumnInfo(name = "user_avatar_url")
    val avatarUrl: String,
    @ColumnInfo(name = "user_is_bot")
    val isBot: Boolean
)

fun UserEntity.mapToUser(): User {
    return User(
        id = id,
        name = name,
        email = email,
        about = about,
        avatarUrl = avatarUrl,
        isBot = isBot
    )
}

fun User.mapToUserEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        about = about,
        avatarUrl = avatarUrl,
        isBot = isBot
    )
}