package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.core.Topic
import com.theost.workchat.data.models.dto.mapToTopic
import com.theost.workchat.network.RetrofitHelper
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object TopicsRepository {

    private val service = RetrofitHelper.retrofitService

    private val topics = mutableListOf<Topic>()

    fun getTopics(channelId: Int): Single<RxResource<List<Topic>>> {
        return service.getChannelTopics(channelId)
            .map { RxResource.success(it.topics.map { topic -> topic.mapToTopic() }) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    topics.clear()
                    topics.addAll(it.data)
                }
            }.subscribeOn(Schedulers.io())
    }

    fun getTopic(id: Int): Single<RxResource<Topic>> {
        return Single.just(topics.toList())
            .map { list -> RxResource.success(list[0]) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

}