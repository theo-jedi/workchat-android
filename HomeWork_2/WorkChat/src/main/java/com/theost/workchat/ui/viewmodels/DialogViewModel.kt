package com.theost.workchat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListDate
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.data.models.ui.ListMessageReaction
import com.theost.workchat.data.repositories.*
import com.theost.workchat.ui.interfaces.DelegateItem
import com.theost.workchat.utils.DateUtils
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class DialogViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<DelegateItem>>()
    val allData: LiveData<List<DelegateItem>> = _allData

    private val _dialogInfo = MutableLiveData<Pair<String, String>>()
    val dialogInfo: LiveData<Pair<String, String>> = _dialogInfo

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    private val _sendingMessageStatus = MutableLiveData<ResourceStatus>()
    val sendingMessageStatus: LiveData<ResourceStatus> = _sendingMessageStatus

    private val _sendingReactionStatus = MutableLiveData<ResourceStatus>()
    val sendingReactionStatus: LiveData<ResourceStatus> = _sendingReactionStatus

    private var userId = -1
    private var dialogId = -1

    fun loadData(dialogId: Int, userId: Int) {
        _loadingStatus.postValue(ResourceStatus.LOADING)
        Single.zip(
            ChannelsRepository.getChannel(dialogId),
            TopicsRepository.getTopic(dialogId),
            MessagesRepository.getMessages(dialogId),
            ReactionsRepository.getReactions(dialogId),
            UsersRepository.getUsers()
        ) { channelsResource, topicsResource, messagesResource, reactionsResource, usersResource ->
            val error = messagesResource.error ?: channelsResource.error ?: topicsResource.error
            ?: reactionsResource.error ?: usersResource.error
            if (error == null) {
                RxResource.success(
                    Pair(
                        Pair(channelsResource.data, topicsResource.data),
                        Triple(messagesResource.data, reactionsResource.data, usersResource.data)
                    )
                )
            } else {
                RxResource.error(error, null)
            }
        }.subscribeOn(Schedulers.io()).subscribe({ resource ->
            val data = resource.data as Pair
            val channel = data.first.first!!
            val topic = data.first.second!!
            val messages = data.second.first!!.sortedBy { it.date }
            val reactions = data.second.second!!
            val users = data.second.third!!

            val listItems = mutableListOf<DelegateItem>()

            for (i in messages.indices) {
                val message = messages[i]
                val user = users.find { it.id == message.userId }
                val type = if (message.userId == userId) MessageType.OUTCOME else MessageType.INCOME
                val listReactions = mutableListOf<ListMessageReaction>()

                if (i == 0 || !DateUtils.isSameDay(message.date, messages[i - 1].date)) {
                    listItems.add(ListDate(DateUtils.getDayDate(message.date)))
                }
                listReactions.addAll(reactions.filter { it.messageId == message.id }.map { reaction ->
                    ListMessageReaction(
                        reaction.id,
                        reaction.emoji,
                        reaction.userIds.size,
                        reaction.userIds.contains(userId)
                    )
                })
                listItems.add(
                    ListMessage(
                        message.id,
                        user?.avatar,
                        user?.name ?: "Неизвестно",
                        message.text,
                        DateUtils.getTime(message.date),
                        listReactions,
                        type
                    )
                )
            }

            _allData.postValue(listItems)
            _dialogInfo.postValue(Pair(channel.name, topic.name))
            _loadingStatus.postValue(resource.status)
        }, {
            it.printStackTrace()
            _loadingStatus.postValue(ResourceStatus.ERROR)
        })
        this.userId = userId
        this.dialogId = dialogId
    }

    fun sendMessage(message: String) {
        _sendingMessageStatus.postValue(ResourceStatus.LOADING)
        MessagesRepository.addMessage(userId, dialogId, message).subscribe({
            _sendingMessageStatus.postValue(ResourceStatus.SUCCESS)
        }, {
            _sendingMessageStatus.postValue(ResourceStatus.ERROR)
        })
    }

    fun updateReaction(dialogId: Int, messageId: Int, reactionId: Int, emoji: String? = null) {
        _sendingReactionStatus.postValue(ResourceStatus.LOADING)
        ReactionsRepository.updateReaction(reactionId, userId, dialogId, messageId, emoji)
            .subscribe({
                _sendingReactionStatus.postValue(ResourceStatus.SUCCESS)
            }, {
                _sendingReactionStatus.postValue(ResourceStatus.ERROR)
            })
    }

}