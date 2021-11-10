package com.theost.workchat.ui.viewmodels

import android.text.SpannableString
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListDate
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.data.models.ui.ListMessageReaction
import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.data.repositories.ReactionsRepository
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.ui.interfaces.DelegateItem
import com.theost.workchat.utils.DateUtils
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class DialogViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<DelegateItem>>()
    val allData: LiveData<List<DelegateItem>> = _allData

    private val _titleData = MutableLiveData<Pair<String, String>>()
    val titleData: LiveData<Pair<String, String>> = _titleData

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    private val _sendingMessageStatus = MutableLiveData<ResourceStatus>()
    val sendingMessageStatus: LiveData<ResourceStatus> = _sendingMessageStatus

    private val _sendingReactionStatus = MutableLiveData<ResourceStatus>()
    val sendingReactionStatus: LiveData<ResourceStatus> = _sendingReactionStatus

    private var channelName: String = ""
    private var topicName: String = ""

    private var numBefore: Int = 100
    private var numAfter: Int = 0

    fun loadMessages(channelName: String, topicName: String) {
        if (channelName != "" && topicName != "") {
            this.channelName = channelName
            this.topicName = topicName
        }

        _titleData.postValue(Pair(channelName, topicName))
        _loadingStatus.postValue(ResourceStatus.LOADING)

        val narrow = "[{\"operator\":\"stream\",\"operand\":\"$channelName\"}," +
                "{\"operator\":\"topic\",\"operand\":\"$topicName\"}]" // todo fix this

        Single.zip(
            MessagesRepository.getMessages(numBefore, numAfter, narrow),
            UsersRepository.getUser()
        ) { messagesResource, userResource ->
            val error = messagesResource.error ?: userResource.error
            if (error == null) {
                RxResource.success(Pair(messagesResource.data, userResource.data))
            } else {
                RxResource.error(error, null)
            }
        }.subscribeOn(Schedulers.io()).subscribe({ resource ->
            if (resource.data?.first != null) {
                val messages = resource.data.first!!
                val userId = resource.data.second!!.id
                val listItems = mutableListOf<DelegateItem>()

                (messages.indices).forEach { index ->
                    val message = messages[index]
                    val reactions = message.reactions

                    val userReactions = reactions.filter { it.userId == userId }.map { it.emoji }
                    val listReactions = mutableListOf<ListMessageReaction>()

                    // Map reactions
                    reactions.distinctBy { it.emoji }.forEach { reaction ->
                        listReactions.add(
                            ListMessageReaction(
                                name = reaction.name,
                                emoji = reaction.emoji,
                                count = reactions.count { it.emoji == reaction.emoji },
                                isSelected = userReactions.contains(reaction.emoji)
                            )
                        )
                    }

                    // Add date item
                    if (index == 0 || DateUtils.notSameDay(message.time, messages[index - 1].time)) {
                        listItems.add(ListDate(DateUtils.getDayDate(message.time)))
                    }

                    //  Add message item
                    val listMessage = ListMessage(
                        id = message.id,
                        senderName = message.senderName,
                        content = SpannableString(
                            HtmlCompat.fromHtml(
                                message.content,
                                HtmlCompat.FROM_HTML_MODE_COMPACT
                            ).trim()
                        ),
                        senderAvatarUrl = message.senderAvatarUrl,
                        time = DateUtils.getTime(message.time),
                        reactions = listReactions.sortedByDescending { it.count },
                        messageType = if (message.senderId == userId) MessageType.OUTCOME else MessageType.INCOME
                    )

                    listItems.add(listMessage)
                }
                _allData.postValue(listItems)
                _loadingStatus.postValue(resource.status)
            } else {
                resource.error?.printStackTrace()
                _loadingStatus.postValue(ResourceStatus.ERROR)
            }
        }, {
            it.printStackTrace()
            _loadingStatus.postValue(ResourceStatus.ERROR)
        })
    }

    fun addMessage(message: String) {
        _sendingMessageStatus.postValue(ResourceStatus.LOADING)
        MessagesRepository.addMessage(channelName, topicName, message).subscribe({
            _sendingMessageStatus.postValue(ResourceStatus.SUCCESS)
        }, {
            _sendingMessageStatus.postValue(ResourceStatus.ERROR)
        })
    }

    fun addReaction(messageId: Int, reactionName: String) {
        _sendingReactionStatus.postValue(ResourceStatus.LOADING)
        ReactionsRepository.addReaction(
            messageId = messageId,
            reactionName = reactionName
        ).subscribe({
            _sendingReactionStatus.postValue(ResourceStatus.SUCCESS)
        }, {
            _sendingReactionStatus.postValue(ResourceStatus.ERROR)
        })
    }

    fun removeReaction(messageId: Int, reactionName: String) {
        _sendingReactionStatus.postValue(ResourceStatus.LOADING)
        ReactionsRepository.removeReaction(
            messageId = messageId,
            reactionName = reactionName
        ).subscribe({
            _sendingReactionStatus.postValue(ResourceStatus.SUCCESS)
        }, {
            _sendingReactionStatus.postValue(ResourceStatus.ERROR)
        })
    }

}