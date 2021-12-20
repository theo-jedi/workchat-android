package com.theost.workchat.data.repositories

import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.core.Message
import com.theost.workchat.data.models.state.ResourceType
import com.theost.workchat.database.entities.mapToMessage
import com.theost.workchat.database.entities.mapToMessageEntity
import com.theost.workchat.network.api.RetrofitHelper
import com.theost.workchat.network.dto.mapToMessage
import com.theost.workchat.utils.StringUtils
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object MessagesRepository {

    private const val CACHE_DIALOG_SIZE = 50
    private const val DIALOG_PAGE_SIZE = 20
    const val DIALOG_NEXT_PAGE = 5

    private val service = RetrofitHelper.retrofitService

    fun getMessages(
        channelName: String,
        topicName: String,
        resourceType: ResourceType
    ): Observable<Result<List<Message>>> {
        return if (resourceType == ResourceType.CACHE_AND_SERVER) {
            Observable.concat(
                getMessagesFromCache(channelName, topicName).toObservable(),
                getMessagesFromServer(channelName, topicName).toObservable()
            )
        } else {
            getMessagesFromServer(channelName, topicName).toObservable()
        }
    }

    private fun getMessagesFromServer(
        channelName: String,
        topicName: String
    ): Single<Result<List<Message>>> {
        return service.getMessages(
            numBefore = DIALOG_PAGE_SIZE,
            numAfter = 0,
            narrow = StringUtils.namesToNarrow(channelName, topicName)
        ).map { response ->
            Result.success(response.messages
                .sortedBy { message -> message.timestamp }
                .map { messageDto -> messageDto.mapToMessage() }
                .reversed()
            )
        }.onErrorReturn {
            Result.failure(it)
        }.doOnSuccess { result ->
            if (result.isSuccess) {
                val messages = result.getOrNull()
                if (messages != null) addMessagesToDatabase(channelName, topicName, messages)
            }
        }.subscribeOn(Schedulers.io())
    }

    fun getMessagesFromServer(
        channelName: String,
        topicName: String,
        lastMessageId: Int
    ): Single<Result<List<Message>>> {
        return service.getMessages(
            numBefore = DIALOG_PAGE_SIZE,
            numAfter = 0,
            narrow = StringUtils.namesToNarrow(channelName, topicName),
            anchor = lastMessageId
        ).map { response ->
            Result.success(response.messages
                .sortedBy { messageDto -> messageDto.timestamp }
                .map { messageDto -> messageDto.mapToMessage() }
                .reversed()
            )
        }.onErrorReturn {
            Result.failure(it)
        }.doOnSuccess { result ->
            if (result.isSuccess) {
                val messages = result.getOrNull()
                if (messages != null) addMessagesToDatabase(channelName, topicName, messages)
            }
        }.subscribeOn(Schedulers.io())
    }

    fun getMessageFromServer(
        channelName: String,
        topicName: String,
        messageId: Int
    ): Single<Result<Message>> {
        return service.getMessages(
            numBefore = 0,
            numAfter = 0,
            narrow = StringUtils.namesToNarrow(channelName, topicName),
            anchor = messageId
        ).map { response ->
            Result.success(response.messages.first().mapToMessage())
        }.onErrorReturn {
            Result.failure(it)
        }.doOnSuccess { result ->
            if (result.isSuccess) {
                val message = result.getOrNull()
                if (message != null) addMessagesToDatabase(channelName, topicName, listOf(message))
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun getMessagesFromCache(
        channelName: String,
        topicName: String
    ): Single<Result<List<Message>>> {
        return WorkChatApp.cacheDatabase.messagesDao().getDialogMessages(channelName, topicName)
            .map { messages ->
                Result.success(messages
                    .sortedBy { messageEntity -> messageEntity.time }
                    .takeLast(CACHE_DIALOG_SIZE)
                    .map { message -> message.mapToMessage() }
                    .reversed()
                )
            }
            .onErrorReturn { Result.failure(it) }
            .subscribeOn(Schedulers.io())
    }

    private fun addMessagesToDatabase(
        channelName: String,
        topicName: String,
        messages: List<Message>
    ) {
        WorkChatApp.cacheDatabase.messagesDao()
            .insertAll(messages.map { message -> message.mapToMessageEntity(channelName, topicName) })
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun addMessage(channelName: String, topicName: String, content: String): Completable {
        return service.addMessage(
            stream = channelName,
            topic = topicName,
            content = content
        ).subscribeOn(Schedulers.io())
    }

}