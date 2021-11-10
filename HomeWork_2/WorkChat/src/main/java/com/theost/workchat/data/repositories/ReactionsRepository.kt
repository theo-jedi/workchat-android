package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.Reaction
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.dto.mapToReactions
import com.theost.workchat.network.RetrofitHelper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object ReactionsRepository {

    private val service = RetrofitHelper.retrofitService

    fun getReactions(): Single<RxResource<List<Reaction>>> {
        return service.getReactions()
            .map { RxResource.success(it.mapToReactions()) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    // todo Room
                }
            }.subscribeOn(Schedulers.io())
    }

    fun addReaction(messageId: Int, reactionName: String): Completable {
        return service.addReaction(
            messageId = messageId,
            emojiName = reactionName
        ).subscribeOn(Schedulers.io())
    }

    fun removeReaction(messageId: Int, reactionName: String): Completable {
        return service.removeReaction(
            messageId = messageId,
            emojiName = reactionName
        ).subscribeOn(Schedulers.io())
    }

}