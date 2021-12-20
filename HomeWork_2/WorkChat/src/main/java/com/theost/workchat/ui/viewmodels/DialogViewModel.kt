package com.theost.workchat.ui.viewmodels

import android.text.SpannableString
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.core.Message
import com.theost.workchat.data.models.core.Reaction
import com.theost.workchat.data.models.core.RxResource
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
import io.reactivex.rxjava3.core.Observable
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

    private val _sendingReactionData = MutableLiveData<Pair<ResourceStatus, Int>>()
    val sendingReactionData: LiveData<Pair<ResourceStatus, Int>> = _sendingReactionData

    private val messagesCache = mutableListOf<Message>()

    private var channelName: String = ""
    private var topicName: String = ""
    private var currentUserId: Int = 0

    private var numBefore: Int = MessagesRepository.DIALOG_PAGE_SIZE
    private var numAfter: Int = 0

    fun loadData(dialogChannelName: String, dialogTopicName: String, dialogUserId: Int) {
        if (dialogChannelName != "") this.channelName = dialogChannelName
        if (dialogTopicName != "") this.topicName = dialogTopicName
        if (dialogUserId != 0) this.currentUserId = dialogUserId

        _titleData.postValue(Pair(channelName, topicName))

        loadMessages()
    }

    fun loadMessages() {
        _loadingStatus.postValue(ResourceStatus.LOADING)

        val lastMessageId = if (messagesCache.isNotEmpty()) messagesCache.last().id else -1
        val resourceType = if (_allData.value == null) ResourceType.CACHE_AND_SERVER else ResourceType.SERVER

        Observable.zip(
            MessagesRepository.getMessages(
                channelName,
                topicName,
                lastMessageId,
                numBefore,
                numAfter,
                resourceType
            ),
            ReactionsRepository.getReactions()
        ) { messagesResource, reactionsResource ->
            val error = messagesResource.error
            if (error == null) {
                val messages = mutableListOf<Message>().apply {
                    addAll(messagesCache)
                    addAll(messagesResource.data ?: mutableListOf())
                }.distinct()
                RxResource.success(Pair(messages, reactionsResource.data))
            } else {
                RxResource.error(error, null)
            }
        }.subscribeOn(Schedulers.io()).subscribe({ resource ->
            if (resource.data != null && resource.data.first.isNotEmpty()) {
                val messages = resource.data.first
                val emojis = resource.data.second

                messagesCache.clear()
                messagesCache.addAll(messages)
                if (messages.last().id == lastMessageId) {
                    _paginationStatus.postValue(PaginationStatus.FULLY_LOADED)
                } else {
                    _paginationStatus.postValue(PaginationStatus.SUCCESS)
                }

                processMessages(messages, emojis)
            } else {
                resource.error?.printStackTrace()
                //_loadingStatus.postValue(ResourceStatus.ERROR)
                if (_paginationStatus.value == PaginationStatus.LOADING) {
                    _paginationStatus.postValue(PaginationStatus.ERROR)
                }
            }
        },
            {
                it.printStackTrace()
                //_loadingStatus.postValue(ResourceStatus.ERROR)
                if (_paginationStatus.value == PaginationStatus.LOADING) {
                    _paginationStatus.postValue(PaginationStatus.ERROR)
                }
            })
    }

    fun updateMessage(messageId: Int) {
        Observable.zip(
            MessagesRepository.getMessages(
                channelName = channelName,
                topicName = topicName,
                lastMessageId = messageId,
                numBefore = 0,
                numAfter = 0,
                resourceType = ResourceType.SERVER
            ),
            ReactionsRepository.getReactions()
        ) { messagesResource, reactionsResource ->
            val error = messagesResource.error
            if (error == null) {
                val messages = mutableListOf<Message>().apply {
                    val message = messagesResource.data!!.first()
                    val index = messagesCache.indexOfFirst { it.id == message.id }
                    messagesCache.removeAt(index)
                    messagesCache.add(index, message)
                    addAll(messagesCache)
                }.distinct()
                RxResource.success(Pair(messages, reactionsResource.data))
            } else {
                RxResource.error(error, null)
            }
        }.subscribeOn(Schedulers.io()).subscribe({ resource ->
            if (resource.data != null && resource.data.first.isNotEmpty()) {
                val messages = resource.data.first
                val emojis = resource.data.second
                processMessages(messages, emojis)
            } else {
                resource.error?.printStackTrace()
                //_loadingStatus.postValue(ResourceStatus.ERROR)
                if (_paginationStatus.value == PaginationStatus.LOADING) {
                    _paginationStatus.postValue(PaginationStatus.ERROR)
                }
            }
        },
            {
                it.printStackTrace()
                //_loadingStatus.postValue(ResourceStatus.ERROR)
                if (_paginationStatus.value == PaginationStatus.LOADING) {
                    _paginationStatus.postValue(PaginationStatus.ERROR)
                }
            })
    }

    private fun processMessages(messages: List<Message>, emojis: List<Reaction>?) {
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

            val messageContent = SpannableString(
                HtmlCompat.fromHtml(
                    if (message.content.contains(":")) {
                        var isFirstColon = false
                        message.content.split(":").joinToString("") {
                            val emoji = emojis?.find { emoji -> emoji.name == it }
                            val content = if (isFirstColon) ":$it" else it
                            isFirstColon = emoji?.emoji == null
                            emoji?.emoji ?: content
                        }
                    } else {
                        message.content
                    },
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                ).trim()
            )

            //  Add message item
            val listMessage = ListMessage(
                id = message.id,
                senderName = message.senderName,
                content = messageContent,
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

        _allData.postValue(listItems)
        _loadingStatus.postValue(ResourceStatus.SUCCESS)
    }

    fun loadNextMessages() {
        if (paginationStatus.value != PaginationStatus.LOADING
            && paginationStatus.value != PaginationStatus.FULLY_LOADED
            && loadingStatus.value != ResourceStatus.LOADING
            && loadingStatus.value != ResourceStatus.ERROR
        ) {
            _paginationStatus.postValue(PaginationStatus.LOADING)
            loadMessages()
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
        _sendingReactionData.postValue(Pair(ResourceStatus.LOADING, messageId))
        ReactionsRepository.addReaction(
            messageId = messageId,
            reactionName = reactionName
        ).subscribe({
            _sendingReactionData.postValue(Pair(ResourceStatus.SUCCESS, messageId))
        }, {
            _sendingReactionData.postValue(Pair(ResourceStatus.ERROR, messageId))
        })
    }

    fun removeReaction(
        messageId: Int,
        reactionName: String,
        reactionCode: String,
        reactionType: String
    ) {
        _sendingReactionData.postValue(Pair(ResourceStatus.LOADING, messageId))
        ReactionsRepository.removeReaction(
            messageId = messageId,
            reactionName = reactionName,
            reactionCode = reactionCode,
            reactionType = reactionType
        ).subscribe({
            _sendingReactionData.postValue(Pair(ResourceStatus.SUCCESS, messageId))
        }, {
            _sendingReactionData.postValue(Pair(ResourceStatus.ERROR, messageId))
        })
    }

}