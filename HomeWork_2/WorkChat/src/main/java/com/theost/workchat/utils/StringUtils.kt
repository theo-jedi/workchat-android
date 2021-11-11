package com.theost.workchat.utils

object StringUtils {

    private const val UNKNOWN_EMOJI = "⬜"

    @Throws(NumberFormatException::class)
    fun utfToEmoji(code: String): String {
        var emojiCode = code
        if (emojiCode.contains("-")) emojiCode = code.split("-")[1]
        return String(Character.toChars(Integer.parseInt(emojiCode, 16)))
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

}