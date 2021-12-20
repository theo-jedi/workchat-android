package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.EmojiReaction
import com.theost.workchat.data.models.core.Reaction
import com.theost.workchat.data.models.core.RxResource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

object ReactionsRepository {

    private val reactions = mutableListOf<Reaction>()
    private val emojiReactions = mutableListOf(
        EmojiReaction(100, "\uD83D\uDE0B"),
        EmojiReaction(101, "\uD83D\uDE0D"),
        EmojiReaction(102, "\uD83D\uDE00"),
        EmojiReaction(103, "\uD83D\uDE03"),
        EmojiReaction(104, "\uD83D\uDE09"),
        EmojiReaction(105, "\uD83D\uDE07"),
        EmojiReaction(106, "\uD83E\uDD29"),
        EmojiReaction(107, "\uD83D\uDE1B"),
        EmojiReaction(108, "\uD83E\uDD11"),
        EmojiReaction(109, "\uD83D\uDE36"),
        EmojiReaction(1010,"\uD83D\uDE44"),
        EmojiReaction(1011,"\uD83D\uDE26"),
        EmojiReaction(1012,"\uD83E\uDD7A"),
        EmojiReaction(1013,"\uD83D\uDE1E")
    )

    fun getEmojiReactions() : Single<RxResource<List<EmojiReaction>>> {
        return Single.just(emojiReactions.toList())
            .map { RxResource.success(it) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    emojiReactions.clear()
                    emojiReactions.addAll(it.data)
                }
            }.subscribeOn(Schedulers.io())
    }

    fun getDialogReactions(dialogId: Int): Single<RxResource<List<Reaction>>> {
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