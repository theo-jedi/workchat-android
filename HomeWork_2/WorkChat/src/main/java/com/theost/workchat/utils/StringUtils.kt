package com.theost.workchat.utils

import com.theost.workchat.network.dto.NarrowDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

object StringUtils {

    private const val UNKNOWN_EMOJI = "⬜"

    @Throws(NumberFormatException::class)
    fun simpleToEmoji(code: String): String {
        return String(Character.toChars(Integer.parseInt(code, 16)))
    }

    fun combinedToEmoji(code: String): String {
        val codes = code.split("-")
        return simpleToEmoji(codes[0]) + simpleToEmoji(codes[1])
    }

    fun utfToEmoji(code: String): String {
        return if (code.contains("-")) {
            combinedToEmoji(code)
        } else {
            simpleToEmoji(code)
        }
    }

    fun codeToEmoji(code: String): String {
        return try {
            utfToEmoji(code)
        } catch (e: NumberFormatException) {
            UNKNOWN_EMOJI
        }
    }

    fun jsonToEmoji(code: String): String {
        return try {
            codeToEmoji(code.replace("\"", ""))
        } catch (e: NumberFormatException) {
            ""
        }
    }

    fun containsQuery(text: String, query: String): Boolean {
        return text.trim().lowercase().contains(query.trim().lowercase())
    }

    fun namesToNarrow(channelName: String, topicName: String) : String {
        return Json.encodeToString(
            serializer(),
            listOf(
                NarrowDto("stream", channelName),
                NarrowDto("topic", topicName)
            )
        )
    }

}