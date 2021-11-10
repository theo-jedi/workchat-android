package com.theost.workchat.utils

import kotlinx.serialization.json.JsonElement

object StringUtils {

    fun codeToEmoji(code: String): String {
        return String(Character.toChars(Integer.parseInt(code, 16)))
    }

    fun jsonToEmoji(jsonElement: JsonElement?): String {
        if (jsonElement != null) {
            val emojiCode = jsonElement.toString()
            if (!emojiCode.contains("-")) {
                return codeToEmoji(emojiCode.replace("\"",""))
            }
        }
        return ""
    }

}