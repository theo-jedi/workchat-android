package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.Reaction
import java.util.*

/* Todo RxJava */
object ReactionsRepository {

    private val reactions = mutableListOf<Reaction>()

    fun getReactions(messageId: Int): List<Reaction> {
        return reactions.filter { it.messageId == messageId }
    }

    fun addReaction(id: Int, userId: Int, messageId: Int, emoji: String): Boolean {
        return simulateReactionCreation(id, userId, messageId, emoji)
    }

    fun editReaction(id: Int, userId: Int, messageId: Int): Boolean {
        return simulateReactionEditing(id, userId, messageId)
    }

    fun removeReaction(id: Int, messageId: Int, userId: Int): Boolean {
        return simulateReactionDeletion(id, messageId, userId)
    }

    fun removeReactions(messageId: Int): Boolean {
        reactions.removeAll { it.messageId == messageId }
        return true
    }

    private fun simulateReactionCreation(id: Int, userId: Int, messageId: Int, emoji: String): Boolean {
        println(reactions)
        val databaseReaction = reactions.find { it.messageId == messageId && it.id == id }
        val reaction: Reaction
        if (databaseReaction == null) {
            reaction = Reaction(
                id = id,
                date = Date().time,
                messageId = messageId,
                emoji = emoji,
                userIds = mutableListOf(userId)
            )
        } else {
            if (databaseReaction.userIds.contains(userId)) return false
            val userIds = mutableSetOf(userId)
            userIds.addAll(databaseReaction.userIds)
            reaction = Reaction(
                id = databaseReaction.id,
                date = databaseReaction.date,
                messageId = databaseReaction.messageId,
                emoji = databaseReaction.emoji,
                userIds = userIds.toList()
            )
            reactions.remove(databaseReaction)
        }
        reactions.add(reaction)
        return true
    }

    private fun simulateReactionEditing(id: Int, userId: Int, messageId: Int): Boolean {
        val databaseReaction = reactions.find { it.messageId == messageId && it.id == id }
        if (databaseReaction != null) {
            val userIds = databaseReaction.userIds.toMutableList()
            if (userIds.contains(userId)) {
                userIds.remove(userId)
            } else {
                userIds.add(userId)
            }
            val reaction = Reaction(
                id = databaseReaction.id,
                date = databaseReaction.date,
                messageId = databaseReaction.messageId,
                emoji = databaseReaction.emoji,
                userIds = userIds
            )
            reactions.remove(databaseReaction)
            if (userIds.size > 0) reactions.add(reaction)
            return true
        } else {
            return false
        }
    }

    private fun simulateReactionDeletion(id: Int, messageId: Int, userId: Int): Boolean {
        val databaseReaction = reactions.find { it.messageId == messageId && it.id == id }
        return if (databaseReaction?.userIds?.contains(userId) == true) {
            val userIds = databaseReaction.userIds.toMutableList()
            userIds.removeAll { it == userId }
            val reaction = Reaction(
                id = databaseReaction.id,
                date = databaseReaction.date,
                messageId = databaseReaction.messageId,
                emoji = databaseReaction.emoji,
                userIds = userIds.toList()
            )
            reactions.remove(databaseReaction)
            reactions.add(reaction)
            true
        } else {
            false
        }
    }

}