package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.Channel
import com.theost.workchat.data.models.core.RxResource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object ChannelsRepository {

    private val channels = mutableListOf(
        Channel(0, "#general", listOf(0, 1)),
        Channel(1, "#development", listOf(2, 3)),
        Channel(2, "#hr", listOf(4, 5))
    )

    fun getChannels(): Single<RxResource<List<Channel>>> {
        return Single.just(channels.toList())
            .map { RxResource.success(it) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    channels.clear()
                    channels.addAll(it.data)
                }
            }.subscribeOn(Schedulers.io())
    }

    fun getChannel(topicId: Int): Single<RxResource<Channel>> {
        return Single.just(channels.toList())
            .map { list -> RxResource.success(list.find { it.topics.contains(topicId) }) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

}