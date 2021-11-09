package com.theost.workchat.ui.viewmodels

import android.text.SpannableString
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListDate
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.data.models.ui.ListMessageReaction
import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.ui.interfaces.DelegateItem
import com.theost.workchat.utils.DateUtils

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
        this.channelName = channelName
        this.topicName = topicName

        if (channelName != "" && topicName != "") {
            _titleData.postValue(Pair(channelName, topicName))
        }

        _loadingStatus.postValue(ResourceStatus.LOADING)
        val narrow =
            "[{\"operator\":\"stream\",\"operand\":\"$channelName\"},{\"operator\":\"topic\",\"operand\":\"$topicName\"}]" // todo fix this
        MessagesRepository.getMessages(numBefore, numAfter, narrow).subscribe({ resource ->
            if (resource.data != null) {
                val messages = resource.data
                val listItems = mutableListOf<DelegateItem>()
                (messages.indices).forEach { index ->
                    val message = messages[index]
                    val reactions = message.reactions
                    val listReactions = mutableListOf<ListMessageReaction>()
                    val selectedReactions = reactions.map { }
                    (message.reactions).forEach { reaction ->
                        if (listReactions.indexOfFirst { it.emoji == reaction } == -1) {
                            val count = reactions.count { it == reaction }
                            listReactions.add(
                                ListMessageReaction(
                                    0,
                                    reaction,
                                    count,
                                    false
                                )
                            )
                        }
                    }

                    if (index == 0 || !DateUtils.isSameDay(
                            message.time,
                            messages[index - 1].time
                        )
                    ) {
                        listItems.add(ListDate(DateUtils.getDayDate(message.time)))
                    }
                    val content = SpannableString(
                        HtmlCompat.fromHtml(
                            message.text,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        ).trim()
                    )
                    listItems.add(
                        ListMessage(
                            id = message.id,
                            name = message.name,
                            text = content,
                            avatar = message.avatarUrl,
                            time = DateUtils.getTime(message.time),
                            reactions = listReactions,
                            messageType = message.messageType
                        )
                    )
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

    fun sendMessage(message: String) {
        _sendingMessageStatus.postValue(ResourceStatus.LOADING)
        MessagesRepository.addMessage(channelName, topicName, message).subscribe({
            _sendingMessageStatus.postValue(ResourceStatus.SUCCESS)
        }, {
            _sendingMessageStatus.postValue(ResourceStatus.ERROR)
        })
    }

    fun updateReaction(dialogId: Int, messageId: Int, reactionId: Int, emoji: String? = null) {
        /*_sendingReactionStatus.postValue(ResourceStatus.LOADING)
        ReactionsRepository.updateReaction(reactionId, userId, dialogId, messageId, emoji)
            .subscribe({
                _sendingReactionStatus.postValue(ResourceStatus.SUCCESS)
            }, {
                _sendingReactionStatus.postValue(ResourceStatus.ERROR)
            })*/
    }

}