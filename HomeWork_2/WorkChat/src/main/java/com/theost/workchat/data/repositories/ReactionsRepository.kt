package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.Reaction
import com.theost.workchat.data.models.core.RxResource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

object ReactionsRepository {

    private val reactions = mutableListOf<Reaction>()

    fun getReactions(dialogId: Int): Single<RxResource<List<Reaction>>> {
        return Single.just(reactions.toList())
            .map { list -> RxResource.success(list.filter { it.dialogId == dialogId }) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

    fun updateReaction(id: Int, userId: Int, dialogId: Int, messageId: Int, emoji: String?) : Completable {
        return Completable.fromAction {
            simulateReactionUpdating(id, userId, dialogId, messageId, emoji)
        }.subscribeOn(Schedulers.io())
    }

    private fun simulateReactionUpdating(id: Int, userId: Int, dialogId: Int, messageId: Int, emoji: String?): Boolean {
        val databaseReaction = reactions.find { it.messageId == messageId && it.id == id }
        val reaction: Reaction
        if (databaseReaction == null) {
            reaction = Reaction(
                id = id,
                date = Date().time,
                dialogId = dialogId,
                messageId = messageId,
                emoji = emoji ?: "",
                userIds = mutableListOf(userId)
            )
            reactions.add(reaction)
        } else {
            if (!databaseReaction.userIds.contains(userId)) {
                val userIds = mutableSetOf(userId)
                userIds.addAll(databaseReaction.userIds)
                reaction = Reaction(
                    id = databaseReaction.id,
                    date = databaseReaction.date,
                    dialogId = dialogId,
                    messageId = databaseReaction.messageId,
                    emoji = databaseReaction.emoji,
                    userIds = userIds.toList()
                )
                reactions.add(reaction)
            }
            reactions.remove(databaseReaction)
        }
        return true
    }

}