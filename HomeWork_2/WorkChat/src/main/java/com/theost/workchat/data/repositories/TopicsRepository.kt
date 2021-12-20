package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.Topic
import com.theost.workchat.database.db.CacheDatabase
import com.theost.workchat.database.entities.mapToTopic
import com.theost.workchat.database.entities.mapToTopicEntity
import com.theost.workchat.network.api.Api
import com.theost.workchat.network.dto.mapToTopic
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TopicsRepository(private val service: Api, database: CacheDatabase) {

    private val topicsDao = database.topicsDao()

    fun getTopics(channelId: Int): Observable<Result<List<Topic>>> {
        return Observable.concat(
            getTopicsFromCache(channelId).toObservable(),
            getTopicsFromServer(channelId).toObservable()
        )
    }

    private fun getTopicsFromServer(channelId: Int): Single<Result<List<Topic>>> {
        return service.getChannelTopics(channelId)
            .map { response -> Result.success(response.topics.map { topicDto -> topicDto.mapToTopic() }) }
            .onErrorReturn { Result.failure(it) }
            .doOnSuccess { result ->
                if (result.isSuccess) {
                    val topics = result.getOrNull()
                    if (topics != null) addTopicsToDatabase(channelId, topics)
                }
            }
            .subscribeOn(Schedulers.io())
    }

    private fun getTopicsFromCache(channelId: Int): Single<Result<List<Topic>>> {
        return topicsDao.getChannelTopics(channelId)
            .map { topics -> Result.success(topics.map { topicEntity -> topicEntity.mapToTopic() }) }
            .onErrorReturn { Result.failure(it) }
            .subscribeOn(Schedulers.io())
    }

    private fun addTopicsToDatabase(channelId: Int, topics: List<Topic>) {
        topicsDao.insertAll(topics.map { topic -> topic.mapToTopicEntity(channelId) })
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

}