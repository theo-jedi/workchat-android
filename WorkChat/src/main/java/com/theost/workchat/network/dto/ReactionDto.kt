package com.theost.workchat.network.dto

import com.theost.workchat.data.models.core.Reaction
import com.theost.workchat.utils.StringUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GetReactionsResponse(
    @SerialName("name_to_codepoint")
    val reactionsObject: JsonObject
)

@Serializable
data class ReactionDto(
    @SerialName("user_id")
    val userId: Int = -1,
    @SerialName("emoji_name")
    val name: String,
    @SerialName("emoji_code")
    val code: String,
    @SerialName("reaction_type")
    val type: String
)

fun GetReactionsResponse.mapToReactions(): List<Reaction> {
    val reactions = mutableListOf<Reaction>()
    val emojis = mutableSetOf<String>()
    reactionsObject.keys.forEach { key ->
        val code = reactionsObject[key].toString()
        val emoji = StringUtils.jsonToEmoji(code)
        if (emoji.isNotEmpty() && !emojis.contains(emoji)) {
            reactions.add(
                Reaction(
                    userId = -1,
                    name = key,
                    code = code,
                    type = "",
                    emoji = emoji
                )
            )
            emojis.add(emoji)
        }
    }
    return reactions
}

fun ReactionDto.mapToReaction(): Reaction {
    return Reaction(
        userId = userId,
        name = name,
        code = code,
        type = type,
        emoji = StringUtils.codeToEmoji(code)
    )
}