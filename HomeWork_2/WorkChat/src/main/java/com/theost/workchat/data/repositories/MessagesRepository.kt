package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.Message
import java.util.*

/* Todo RxJava */
object MessagesRepository {

    private val messages = mutableListOf(
        Message(0, 1, 0, "Hello there!", Date()),
        Message(1, 2, 0, "General Kenobi!", Date())
    )

    fun getMessages(dialogId: Int): List<Message> {
        return messages.filter { it.dialogId == dialogId }
    }

    fun addMessage(userId: Int, dialogId: Int, text: String): Boolean {
        return simulateMessageCreation(userId, dialogId, text)
    }

    fun removeMessage(id: Int): Boolean {
        messages.removeAll { it.id == id }
        return true
    }

    fun removeMessages(ids: List<Int>): Boolean {
        messages.removeAll { it.id in ids }
        return true
    }

    private fun simulateMessageCreation(userId: Int, dialogId: Int, text: String): Boolean {
        messages.add(
            Message(
                id = messages.size,
                userId = userId,
                dialogId = dialogId,
                text = text,
                date = Date()
            )
        )
        return true
    }

}