package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.Message
import com.theost.workchat.data.models.state.ResourceType
import com.theost.workchat.database.db.CacheDatabase
import com.theost.workchat.database.entities.mapToMessage
import com.theost.workchat.database.entities.mapToMessageEntity
import com.theost.workchat.network.api.Api
import com.theost.workchat.network.dto.DeleteMessageResponse
import com.theost.workchat.network.dto.mapToMessage
import com.theost.workchat.utils.ApiUtils
import com.theost.workchat.utils.StringUtils
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*


class MessagesRepository(private val service: Api, database: CacheDatabase) {

    private val messagesDao = database.messagesDao()

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
                .sortedByDescending { message -> message.timestamp }
                .map { messageDto -> messageDto.mapToMessage() }
            )
        }.onErrorReturn {
            Result.failure(it)
        }.flatMap { result ->
            val messages = result.getOrNull()
            if (messages != null) {
                removeMessagesFromDatabase(channelName, topicName)
                    .andThen(addMessagesToDatabase(channelName, topicName, messages))
                    .andThen(Single.just(result))
            } else {
                Single.just(result)
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
                .sortedByDescending { messageDto -> messageDto.timestamp }
                .map { messageDto -> messageDto.mapToMessage() }
            )
        }.onErrorReturn {
            Result.failure(it)
        }.flatMap { result ->
            val messages = result.getOrNull()
            if (messages != null) {
                addMessagesToDatabase(channelName, topicName, messages)
                    .andThen(Single.just(result))
            } else {
                Single.just(result)
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
        }.flatMap { result ->
            val message = result.getOrNull()
            if (message != null) {
                addMessagesToDatabase(channelName, topicName, listOf(message))
                    .andThen(Single.just(result))
            } else {
                Single.just(result)
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun getMessagesFromCache(
        channelName: String,
        topicName: String
    ): Single<Result<List<Message>>> {
        return messagesDao.getLastDialogMessages(channelName, topicName, CACHE_DIALOG_SIZE)
            .map { messages ->
                Result.success(messages
                    .sortedByDescending { messageEntity -> messageEntity.time }
                    .map { message -> message.mapToMessage() }
                    .run { ApiUtils.addEmptyMessage(this) }
                )
            }
            .onErrorReturn { Result.failure(it) }
            .subscribeOn(Schedulers.io())
    }

    private fun addMessagesToDatabase(
        channelName: String,
        topicName: String,
        messages: List<Message>
    ): Completable {
        return messagesDao.insertAll(messages.map { message ->
            message.mapToMessageEntity(
                channelName,
                topicName
            )
        }).subscribeOn(Schedulers.io())
    }

    private fun removeMessagesFromDatabase(channelName: String, topicName: String): Completable {
        return messagesDao.deleteTopicMessages(channelName, topicName)
            .subscribeOn(Schedulers.io())
    }

    private fun removeMessageFromDatabase(messageId: Int): Completable {
        return messagesDao.delete(messageId).subscribeOn(Schedulers.io())
    }

    fun addMessage(channelName: String, topicName: String, content: String): Completable {
        return service.addMessage(
            stream = channelName,
            topic = topicName,
            content = content
        ).subscribeOn(Schedulers.io())
    }

    fun editMessage(messageId: Int, content: String): Completable {
        return service.editMessage(
            messageId = messageId,
            content = content
        ).subscribeOn(Schedulers.io())
    }

    fun deleteMessage(messageId: Int): Single<DeleteMessageResponse> {
        return service.deleteMessage(messageId = messageId)
            .flatMap { response ->
                removeMessageFromDatabase(messageId = messageId)
                    .andThen(Single.just(response))
            }
            .subscribeOn(Schedulers.io())
    }

    fun addPhoto(file: File): Single<Result<String>> {
        val image = file.asRequestBody("image/*".toMediaType())
        val body: MultipartBody.Part =
            MultipartBody.Part.Companion.createFormData(
                file.name,
                UUID.randomUUID().toString() + file.extension,
                image
            )

        return service.addPhoto(body)
            .map { response -> Result.success(response.uri) }
            .onErrorReturn { Result.failure(it) }
            .subscribeOn(Schedulers.io())
    }

    companion object {
        private const val CACHE_DIALOG_SIZE = 50
        const val DIALOG_PAGE_SIZE = 30
        const val DIALOG_NEXT_PAGE = 5
    }

}