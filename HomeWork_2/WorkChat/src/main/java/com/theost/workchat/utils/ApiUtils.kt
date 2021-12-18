package com.theost.workchat.utils

object ApiUtils {

    // This method implements undocumented logic of Zulip API
    fun avatarUrlToMedium(url: String): String {
        return if (url.contains("?")) {
            val urlParts = url.split("?")
            val imageUrl = urlParts[0]
            val queryUrl = urlParts[1]
            "$imageUrl-medium.png?$queryUrl"
        } else {
            url
        }
    }

}