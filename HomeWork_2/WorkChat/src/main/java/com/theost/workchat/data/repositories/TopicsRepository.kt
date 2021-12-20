package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.core.Topic
import com.theost.workchat.data.models.dto.mapToTopic
import com.theost.workchat.network.RetrofitHelper
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object TopicsRepository {

    private val service = RetrofitHelper.retrofitService

    fun getTopics(channelId: Int): Single<RxResource<List<Topic>>> {
        return service.getChannelTopics(
            channelId = channelId
        ).map { RxResource.success(it.topics.map { topic -> topic.mapToTopic() }) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    // todo Room
                }
            }.subscribeOn(Schedulers.io())
    }

}