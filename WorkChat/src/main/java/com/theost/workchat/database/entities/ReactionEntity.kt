package com.theost.workchat.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theost.workchat.data.models.core.Reaction

@Entity(tableName = "reactions")
data class ReactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "code")
    val code: String,
    @ColumnInfo(name = "emoji")
    val emoji: String
)

fun ReactionEntity.mapToReaction(): Reaction {
    return Reaction(
        userId = -1,
        name = name,
        code = code,
        type = "",
        emoji = emoji
    )
}

fun Reaction.mapToReactionEntity(): ReactionEntity {
    return ReactionEntity(
        name = name,
        code = code,
        emoji = emoji
    )
}