package com.theost.workchat.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theost.workchat.data.models.core.Channel

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey
    @ColumnInfo(name = "channel_id")
    val id: Int,
    @ColumnInfo(name = "channel_name")
    val name: String
)

fun ChannelEntity.mapToChannel(): Channel {
    return Channel(
        id = id,
        name = name
    )
}

fun Channel.mapToChannelEntity(): ChannelEntity {
    return ChannelEntity(
        id = id,
        name = name
    )
}