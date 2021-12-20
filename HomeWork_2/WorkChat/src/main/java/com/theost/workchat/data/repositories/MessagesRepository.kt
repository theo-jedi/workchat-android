package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.Message
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.dto.mapToMessage
import com.theost.workchat.network.RetrofitHelper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object MessagesRepository {

    private val service = RetrofitHelper.retrofitService

    fun getMessages(
        numBefore: Int,
        numAfter: Int,
        narrow: String
    ): Single<RxResource<List<Message>>> {
        return service.getMessages(
            numBefore = numBefore,
            numAfter = numAfter,
            narrow = narrow
        ).map { RxResource.success(it.messages.map { message -> message.mapToMessage() }.reversed()) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    // todo Room
                }
            }.subscribeOn(Schedulers.io())
    }

    fun addMessage(channelName: String, topicName: String, content: String): Completable {
        return service.addMessage(
            stream = channelName,
            topic = topicName,
            content = content
        ).subscribeOn(Schedulers.io())
    }

}