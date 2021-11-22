package com.theost.workchat.data.repositories

import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.core.Reaction
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.database.entities.mapToReaction
import com.theost.workchat.database.entities.mapToReactionEntity
import com.theost.workchat.network.api.RetrofitHelper
import com.theost.workchat.network.dto.mapToReactions
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object ReactionsRepository {

    private val service = RetrofitHelper.retrofitService
    private var isCacheUpdated = false

    fun getReactions(): Observable<RxResource<List<Reaction>>> {
        return if (isCacheUpdated) {
            getReactionsFromCache().toObservable()
        } else {
            Observable.concat(
                getReactionsFromCache().toObservable(),
                getReactionsFromServer().toObservable()
            )
        }
    }

    fun getReactionsFromServer(): Single<RxResource<List<Reaction>>> {
        return service.getReactions()
            .map { RxResource.success(it.mapToReactions()) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) addReactionsToDatabase(it.data)
            }.subscribeOn(Schedulers.io())
    }

    fun getReactionsFromCache(): Single<RxResource<List<Reaction>>> {
        return WorkChatApp.cacheDatabase.reactionsDao().getAll()
            .map { RxResource.success(it.map { reaction -> reaction.mapToReaction() }) }
            .onErrorReturn { RxResource.error(it, null) }
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