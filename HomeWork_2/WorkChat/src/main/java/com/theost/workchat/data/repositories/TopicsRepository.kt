package com.theost.workchat.data.repositories

import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.core.Topic
import com.theost.workchat.database.entities.mapToTopic
import com.theost.workchat.database.entities.mapToTopicEntity
import com.theost.workchat.network.api.RetrofitHelper
import com.theost.workchat.network.dto.mapToTopic
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object TopicsRepository {

    private val service = RetrofitHelper.retrofitService

    fun getTopics(channelId: Int): Observable<RxResource<List<Topic>>> {
        return Observable.concat(
            getTopicsFromCache(channelId).toObservable(),
            getTopicsFromServer(channelId).toObservable()
        )
    }

    private fun getTopicsFromServer(channelId: Int): Single<RxResource<List<Topic>>> {
        return service.getChannelTopics(channelId)
            .map { RxResource.success(it.topics.map { topic -> topic.mapToTopic() }) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) addTopicsToDatabase(channelId, it.data)
            }.subscribeOn(Schedulers.io())
    }

    private fun getTopicsFromCache(channelId: Int): Single<RxResource<List<Topic>>> {
        return WorkChatApp.cacheDatabase.topicsDao().getChannelTopics(channelId)
            .map { RxResource.success(it.map { topic -> topic.mapToTopic() }) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

    private fun addTopicsToDatabase(channelId: Int, topics: List<Topic>) {
        WorkChatApp.cacheDatabase.topicsDao()
            .insertAll(topics.map { it.mapToTopicEntity(channelId) })
            .subscribeOn(Schedulers.io()).subscribe()
    }

}