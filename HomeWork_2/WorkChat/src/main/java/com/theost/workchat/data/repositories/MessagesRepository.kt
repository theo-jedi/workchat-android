package com.theost.workchat.data.repositories

import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.core.Message
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.state.ResourceType
import com.theost.workchat.database.entities.mapToMessage
import com.theost.workchat.database.entities.mapToMessageEntity
import com.theost.workchat.network.api.RetrofitHelper
import com.theost.workchat.network.dto.NarrowDto
import com.theost.workchat.network.dto.mapToMessage
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

object MessagesRepository {

    private val service = RetrofitHelper.retrofitService
    private const val CACHE_DIALOG_SIZE = 20
    const val DIALOG_PAGE_SIZE = 20
    const val DIALOG_NEXT_PAGE = 5

    fun getMessages(
        channelName: String,
        topicName: String,
        numBefore: Int,
        numAfter: Int,
        resourceType: ResourceType
    ): Observable<RxResource<List<Message>>> {
        return if (resourceType == ResourceType.CACHE_AND_SERVER) {
            Observable.concat(
                getMessagesFromCache(channelName, topicName).toObservable(),
                getMessagesFromServer(channelName, topicName, numBefore, numAfter).toObservable()
            )
        } else {
            getMessagesFromServer(channelName, topicName, numBefore, numAfter).toObservable()
        }
    }

    private fun getMessagesFromServer(
        channelName: String,
        topicName: String,
        numBefore: Int,
        numAfter: Int
    ): Single<RxResource<List<Message>>> {
        val narrow = Json.encodeToString(
            serializer(),
            listOf(
                NarrowDto("stream", channelName),
                NarrowDto("topic", topicName)
            )
        )
        return service.getMessages(
            numBefore = numBefore,
            numAfter = numAfter,
            narrow = narrow
        ).map {
            RxResource.success(it.messages.map { message -> message.mapToMessage() }
                .sortedBy { message -> message.time }.reversed())
        }.onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) addMessagesToDatabase(channelName, topicName, it.data)
            }.subscribeOn(Schedulers.io())
    }

    private fun getMessagesFromCache(
        channelName: String,
        topicName: String
    ): Single<RxResource<List<Message>>> {
        return WorkChatApp.cacheDatabase.messagesDao().getDialogMessages(channelName, topicName)
            .map {
                RxResource.success(it.map { message -> message.mapToMessage() }
                    .sortedBy { message -> message.time }.reversed())
            }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

    private fun addMessagesToDatabase(
        channelName: String,
        topicName: String,
        messages: List<Message>
    ) {
        WorkChatApp.cacheDatabase.messagesDao()
            .insertAll(messages.take(CACHE_DIALOG_SIZE)
                .map { it.mapToMessageEntity(channelName, topicName) })
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun addMessage(channelName: String, topicName: String, content: String): Completable {
        return service.addMessage(
            stream = channelName,
            topic = topicName,
            content = content
        ).subscribeOn(Schedulers.io())
    }

}