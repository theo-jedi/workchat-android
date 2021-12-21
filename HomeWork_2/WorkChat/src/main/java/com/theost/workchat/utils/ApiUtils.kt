package com.theost.workchat.utils

import com.theost.workchat.data.models.core.Message
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.network.api.ApiConfig
import java.util.*

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

    fun getPhotoUrl(message: String): String {
        return try {
            val url = message.split("href=\"/user_uploads/")[1].split("\"")[0]
            ApiConfig.BASE_URL + "/user_uploads/" + url
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun containsPhoto(message: String): Boolean {
        return message.contains("user_uploads")
    }

    fun getPhotoUriMessage(uri: String): String {
        val name = if (uri.contains("/")) uri.split("/").last() else uri
        return "[$name]($uri)"
    }

    fun isPhotoSizeValid(length: Long): Boolean {
        return length <= ApiConfig.FILE_MAX_LENGTH
    }

    // Returns empty message with negative id
    // Using to highlight cache data
    fun addEmptyMessage(messages: List<Message>): List<Message> {
        return messages.toMutableList().apply {
            add(
                0, Message(
                    id = -1,
                    content = "",
                    date = Date(),
                    senderId = -1,
                    senderName = "",
                    senderAvatarUrl = "",
                    reactions = emptyList()
                )
            )
        }.toList()
    }

    // Removes empty message with negative id
    fun removeEmptyMessage(messages: List<ListMessage>): List<ListMessage> {
        return messages.toMutableList().apply { removeAt(getEmptyMessageIndex(messages)) }.toList()
    }

    fun containsEmptyMessage(messages: List<ListMessage>): Boolean {
        return getEmptyMessageIndex(messages) != -1
    }

    fun getEmptyMessageIndex(messages: List<ListMessage>): Int {
        return messages.indexOfFirst { message -> message.id < 0 }
    }

}