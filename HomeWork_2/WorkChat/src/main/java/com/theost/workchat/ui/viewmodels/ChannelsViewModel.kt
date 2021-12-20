package com.theost.workchat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListChannel
import com.theost.workchat.data.models.ui.ListTopic
import com.theost.workchat.data.repositories.ChannelsRepository
import com.theost.workchat.data.repositories.TopicsRepository
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.ui.interfaces.DelegateItem
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class ChannelsViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<DelegateItem>>()
    val allData: LiveData<List<DelegateItem>> = _allData

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    private var channelsList = listOf<ListChannel>()
    private var topicsList = listOf<ListTopic>()

    fun loadData(userId: Int, channelsType: ChannelsType) {
        _loadingStatus.postValue(ResourceStatus.LOADING)
        Single.zip(
            ChannelsRepository.getChannels(),
            TopicsRepository.getTopics(),
            UsersRepository.getUser(userId)
        ) { channelsResource, topicsResource, usersResource ->
            val error = channelsResource.error ?: topicsResource.error ?: usersResource.error
            if (error == null) {
                RxResource.success(
                    Triple(
                        channelsResource.data,
                        topicsResource.data,
                        usersResource.data
                    )
                )
            } else {
                RxResource.error(error, null)
            }
        }.subscribeOn(Schedulers.io()).subscribe({ resource ->
            val data = resource.data as Triple
            var channels = data.first!!
            val topics = data.second!!
            val user = data.third!!
            if (channelsType == ChannelsType.SUBSCRIBED) {
                val channelsSubscribedList = user.channelsIds
                channels = channels.filter { channel ->
                    channelsSubscribedList.contains(channel.id)
                }
            }
            channelsList = channels.map { channel ->
                ListChannel(
                    id = channel.id,
                    name = channel.name,
                    topics = topics.filter { it.channelId == channel.id }.map { it.id },
                    isSelected = false
                )
            }
            topicsList = topics.map { topic ->
                ListTopic(id = topic.id, name = topic.name, topic.count)
            }
            _allData.postValue(channelsList)
            _loadingStatus.postValue(resource.status)
        }, {
            it.printStackTrace()
            _loadingStatus.postValue(ResourceStatus.ERROR)
        })
    }

    fun updateTopics(channelId: Int, isSelected: Boolean) {
        if (!isSelected) {
            val channelIndex = channelsList.indexOfFirst { it.id == channelId }
            val channel = channelsList[channelIndex].let {
                ListChannel(
                    id = it.id,
                    name = it.name,
                    topics = it.topics,
                    true
                )
            }
            val list = mutableListOf<DelegateItem>().apply {
                addAll(channelsList)
                removeAt(channelIndex)
                add(channelIndex, channel)
                channel.topics.reversed().forEach { topicId ->
                    topicsList.find { it.id == topicId }?.let { topic ->
                        add(channelIndex + 1, topic)
                    }
                }
            }
            _allData.postValue(list)
        } else {
            _allData.postValue(channelsList)
        }
    }

    fun filterData(filter: String) {
        val list = channelsList.filter { channel ->
            channel.name.lowercase().contains(filter.trim().lowercase())
        }
        _allData.postValue(list)
    }

}