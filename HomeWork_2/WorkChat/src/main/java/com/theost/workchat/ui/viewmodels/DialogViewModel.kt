package com.theost.workchat.ui.viewmodels

import android.text.SpannableString
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.data.models.state.PaginationStatus
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.state.ResourceType
import com.theost.workchat.data.models.ui.ListDate
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.data.models.ui.ListMessageReaction
import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.data.repositories.ReactionsRepository
import com.theost.workchat.ui.interfaces.DelegateItem
import com.theost.workchat.utils.DateUtils
import io.reactivex.rxjava3.schedulers.Schedulers

class DialogViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<DelegateItem>>()
    val allData: LiveData<List<DelegateItem>> = _allData

    private val _titleData = MutableLiveData<Pair<String, String>>()
    val titleData: LiveData<Pair<String, String>> = _titleData

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    private val _paginationStatus = MutableLiveData<PaginationStatus>()
    val paginationStatus: LiveData<PaginationStatus> = _paginationStatus

    private val _sendingMessageStatus = MutableLiveData<ResourceStatus>()
    val sendingMessageStatus: LiveData<ResourceStatus> = _sendingMessageStatus

    private val _sendingReactionStatus = MutableLiveData<ResourceStatus>()
    val sendingReactionStatus: LiveData<ResourceStatus> = _sendingReactionStatus

    private var channelName: String = ""
    private var topicName: String = ""

    private var numBefore: Int = MessagesRepository.DIALOG_PAGE_SIZE
    private var numAfter: Int = 0

    private var firstMessageId: Int = 0

    fun loadMessages(channelName: String, topicName: String, currentUserId: Int) {
        if (channelName != "" && topicName != "") {
            this.channelName = channelName
            this.topicName = topicName
        }

        _titleData.postValue(Pair(channelName, topicName))
        _loadingStatus.postValue(ResourceStatus.LOADING)

        val resourceType =
            if (_allData.value == null) ResourceType.CACHE_AND_SERVER else ResourceType.SERVER

        MessagesRepository.getMessages(channelName, topicName, numBefore, numAfter, resourceType)
            .subscribeOn(Schedulers.io()).subscribe({ resource ->
                if (resource.data != null && resource.data.isNotEmpty()) {
                    val messages = resource.data
                    val listItems = mutableListOf<DelegateItem>()

                    (messages.indices).forEach { index ->
                        val message = messages[index]
                        val reactions = message.reactions

                        val userReactions =
                            reactions.filter { it.userId == currentUserId }.map { it.emoji }
                        val listReactions = mutableListOf<ListMessageReaction>()

                        // Map reactions
                        reactions.distinctBy { it.emoji }.forEach { reaction ->
                            listReactions.add(
                                ListMessageReaction(
                                    name = reaction.name,
                                    code = reaction.code,
                                    type = reaction.type,
                                    emoji = reaction.emoji,
                                    count = reactions.count { it.emoji == reaction.emoji },
                                    isSelected = userReactions.contains(reaction.emoji)
                                )
                            )
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
                            messageType = if (message.senderId == currentUserId) MessageType.OUTCOME else MessageType.INCOME
                        )
                        listItems.add(listMessage)

                        // Add date item
                        if (index == messages.size - 1 || DateUtils.notSameDay(
                                message.time,
                                messages[index + 1].time
                            )
                        ) {
                            listItems.add(ListDate(DateUtils.getDayDate(message.time)))
                        }
                    }

                    // Update pagination state
                    if (resourceType == ResourceType.SERVER) {
                        val firstId = messages.last().id
                        if (firstId == firstMessageId) {
                            _paginationStatus.postValue(PaginationStatus.FULLY_LOADED)
                        } else {
                            _paginationStatus.postValue(PaginationStatus.SUCCESS)
                            firstMessageId = firstId
                        }
                    }

                    _allData.postValue(listItems)
                    _loadingStatus.postValue(ResourceStatus.SUCCESS)
                } else {
                    resource.error?.printStackTrace()
                    //_loadingStatus.postValue(ResourceStatus.ERROR)
                    if (_paginationStatus.value == PaginationStatus.LOADING) {
                        _paginationStatus.postValue(PaginationStatus.ERROR)
                    }
                }
            }, {
                it.printStackTrace()
                //_loadingStatus.postValue(ResourceStatus.ERROR)
                if (_paginationStatus.value == PaginationStatus.LOADING) {
                    _paginationStatus.postValue(PaginationStatus.ERROR)
                }
            })
    }

    fun loadNextMessages(channelName: String, topicName: String, currentUserId: Int) {
        if (paginationStatus.value != PaginationStatus.LOADING
            && paginationStatus.value != PaginationStatus.FULLY_LOADED
            && loadingStatus.value != ResourceStatus.LOADING
            && loadingStatus.value != ResourceStatus.ERROR
        ) {
            _paginationStatus.postValue(PaginationStatus.LOADING)
            numBefore += MessagesRepository.DIALOG_PAGE_SIZE
            loadMessages(channelName, topicName, currentUserId)
        }
    }

    fun addMessage(content: String) {
        _sendingMessageStatus.postValue(ResourceStatus.LOADING)
        MessagesRepository.addMessage(
            channelName = channelName,
            topicName = topicName,
            content = content
        ).subscribe({
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

    fun removeReaction(
        messageId: Int,
        reactionName: String,
        reactionCode: String,
        reactionType: String
    ) {
        _sendingReactionStatus.postValue(ResourceStatus.LOADING)
        ReactionsRepository.removeReaction(
            messageId = messageId,
            reactionName = reactionName,
            reactionCode = reactionCode,
            reactionType = reactionType
        ).subscribe({
            _sendingReactionStatus.postValue(ResourceStatus.SUCCESS)
        }, {
            _sendingReactionStatus.postValue(ResourceStatus.ERROR)
        })
    }

}