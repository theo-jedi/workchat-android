package com.theost.workchat.data.repositories

import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.core.Reaction
import com.theost.workchat.database.entities.mapToReaction
import com.theost.workchat.database.entities.mapToReactionEntity
import com.theost.workchat.network.api.RetrofitHelper
import com.theost.workchat.network.dto.mapToReactions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object ReactionsRepository {

    private val service = RetrofitHelper.retrofitService
    private var isCacheUpdated = false

    fun getReactions(): Observable<List<Reaction>> {
        return if (isCacheUpdated) {
            Observable.concat(
                getReactionsFromCache().toObservable(),
                getReactionsFromCache().toObservable()
            )
        } else {
            Observable.concat(
                getReactionsFromCache().toObservable(),
                getReactionsFromServer().toObservable()
            )
        }
    }

    fun getReactionsFromServer(): Single<List<Reaction>> {
        return service.getReactions()
            .map { response -> response.mapToReactions() }
            .doOnSuccess { reactions -> addReactionsToDatabase(reactions) }
            .subscribeOn(Schedulers.io())
    }

    fun getReactionsFromCache(): Single<List<Reaction>> {
        return WorkChatApp.cacheDatabase.reactionsDao().getAll()
            .map { response -> response.map { reaction -> reaction.mapToReaction() } }
            .subscribeOn(Schedulers.io())
    }

    private fun addReactionsToDatabase(reactions: List<Reaction>) {
        WorkChatApp.cacheDatabase.reactionsDao()
            .insertAll(reactions.map { it.mapToReactionEntity() })
            .doOnComplete { isCacheUpdated = true }
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun addReaction(messageId: Int, reactionName: String): Completable {
        return service.addReaction(
            messageId = messageId,
            emojiName = reactionName
        ).subscribeOn(Schedulers.io())
    }

    fun removeReaction(
        messageId: Int,
        reactionName: String,
        reactionCode: String,
        reactionType: String
    ): Completable {
        return service.removeReaction(
            messageId = messageId,
            emojiName = reactionName,
            emojiCode = reactionCode,
            reactionType = reactionType
        ).subscribeOn(Schedulers.io())
    }

}