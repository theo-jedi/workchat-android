package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.Message
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.utils.DateUtils
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

object MessagesRepository {

    private val messages = mutableListOf(
        Message(0, 1, 0, "Hello there!", DateUtils.getRandomDateBefore()),
        Message(1, 2, 0, "General Kenobi!", DateUtils.getRandomDateBefore()),
        Message(2, 2, 0, "Fine addition to my collection!", DateUtils.getRandomDateBefore()),
        Message(3, 1, 0, ":)", DateUtils.getRandomDateBefore())
    )

    fun getMessages(dialogId: Int): Single<RxResource<List<Message>>> {
        return Single.just(messages.toList())
            .map { list -> RxResource.success(list.filter { it.dialogId == dialogId }) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

    fun addMessage(userId: Int, dialogId: Int, text: String): Completable {
        return Completable.fromAction {
            simulateMessageCreation(userId, dialogId, text)
        }.subscribeOn(Schedulers.io())
    }

    private fun simulateMessageCreation(userId: Int, dialogId: Int, text: String): Boolean {
        messages.add(
            Message(
                id = messages.size,
                userId = userId,
                dialogId = dialogId,
                text = text,
                date = Date()
            )
        )
        return true
    }

}