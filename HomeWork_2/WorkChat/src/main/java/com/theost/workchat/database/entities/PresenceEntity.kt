package com.theost.workchat.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theost.workchat.data.models.state.UserStatus

@Entity(tableName = "presence")
data class PresenceEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val id: Int,
    @ColumnInfo(name = "user_status")
    val status: UserStatus
)

fun PresenceEntity.mapToPresence(): UserStatus {
    return status
}

fun UserStatus.mapToPresenceEntity(userId: Int): PresenceEntity {
    return PresenceEntity(
        id = userId,
        status = this,
    )
}