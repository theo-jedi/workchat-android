package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.Reaction

/* Todo RxJava */
object ReactionsRepository {

    private val reactions = mutableListOf<Reaction>()

    fun getReactions(messageId: Int): List<Reaction> {
        return reactions.filter { it.messageId == messageId }
    }

    fun addReaction(id: Int, userId: Int, messageId: Int, emoji: String): Boolean {
        return simulateReactionCreation(id, userId, messageId, emoji)
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
        val databaseReaction = reactions.find { it.id == id }
        val reaction: Reaction
        if (databaseReaction == null) {
            reaction = Reaction(
                id = id,
                messageId = messageId,
                emoji = emoji,
                userIds = mutableListOf(userId)
            )
        } else {
            val userIds = mutableSetOf(userId)
            userIds.addAll(databaseReaction.userIds)
            reaction = Reaction(
                id = databaseReaction.id,
                messageId = databaseReaction.messageId,
                emoji = databaseReaction.emoji,
                userIds = userIds.toList()
            )
            reactions.remove(databaseReaction)
        }
        reactions.add(reaction)
        return true
    }

    private fun simulateReactionDeletion(id: Int, messageId: Int, userId: Int): Boolean {
        val databaseReaction = reactions.find { it.messageId == messageId && it.id == id }
        return if (databaseReaction?.userIds?.contains(userId) == true) {
            val userIds = databaseReaction.userIds.toMutableList()
            userIds.removeAll { it == userId }
            val reaction = Reaction(
                id = databaseReaction.id,
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