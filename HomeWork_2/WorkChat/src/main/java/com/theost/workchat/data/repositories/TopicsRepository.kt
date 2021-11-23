package com.theost.workchat.data.repositories

import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.core.Topic
import com.theost.workchat.database.entities.mapToTopic
import com.theost.workchat.database.entities.mapToTopicEntity
import com.theost.workchat.network.api.RetrofitHelper
import com.theost.workchat.network.dto.mapToTopic
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object TopicsRepository {

    private val service = RetrofitHelper.retrofitService

    fun getTopics(channelId: Int): Observable<List<Topic>> {
        return Observable.concat(
            getTopicsFromCache(channelId).toObservable(),
            getTopicsFromServer(channelId).toObservable()
        )
    }

    private fun getTopicsFromServer(channelId: Int): Single<List<Topic>> {
        return service.getChannelTopics(channelId)
            .map { it.topics.map { topic -> topic.mapToTopic() } }
            .doOnSuccess { topics -> addTopicsToDatabase(channelId, topics) }
            .subscribeOn(Schedulers.io())
    }

    private fun getTopicsFromCache(channelId: Int): Single<List<Topic>> {
        return WorkChatApp.cacheDatabase.topicsDao().getChannelTopics(channelId)
            .map { it.map { topic -> topic.mapToTopic() } }
            .subscribeOn(Schedulers.io())
    }

    private fun addTopicsToDatabase(channelId: Int, topics: List<Topic>) {
        WorkChatApp.cacheDatabase.topicsDao()
            .insertAll(topics.map { it.mapToTopicEntity(channelId) })
            .subscribeOn(Schedulers.io()).subscribe()
    }

}