package com.theost.workchat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListChannel
import com.theost.workchat.data.models.ui.ListTopic
import com.theost.workchat.data.repositories.ChannelsRepository
import com.theost.workchat.data.repositories.TopicsRepository
import com.theost.workchat.ui.interfaces.DelegateItem
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ChannelsViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<DelegateItem>>()
    val allData: LiveData<List<DelegateItem>> = _allData

    private val _subscribedChannelsIds = MutableLiveData<List<Int>>()
    val subscribedChannelsIds: LiveData<List<Int>> = _subscribedChannelsIds

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    private var channelsList = listOf<ListChannel>()

    fun loadData(channelsType: ChannelsType, subscribedChannels: List<Int>) {
        _loadingStatus.postValue(ResourceStatus.LOADING)
        ChannelsRepository.getChannels(channelsType, subscribedChannels)
            .subscribeOn(Schedulers.io()).subscribe({ resource ->
                if (resource.data != null && resource.data.isNotEmpty()) {
                    val channels = resource.data

                    if (channelsType == ChannelsType.SUBSCRIBED) {
                        val subscribedIds = channels.map { it.id }
                        _subscribedChannelsIds.postValue(subscribedIds)
                    }

                    channelsList = channels.map { channel ->
                        ListChannel(
                            id = channel.id,
                            name = channel.name,
                            isSelected = false
                        )
                    }

                    _allData.postValue(channelsList)
                    _loadingStatus.postValue(ResourceStatus.SUCCESS)
                } else {
                    resource.error?.printStackTrace()
                    //_loadingStatus.postValue(ResourceStatus.ERROR)
                }
            }, {
                it.printStackTrace()
                //_loadingStatus.postValue(ResourceStatus.ERROR)
            })
    }

    fun updateTopics(channelId: Int, isSelected: Boolean) {
        val itemsList = mutableListOf<DelegateItem>().apply { addAll(channelsList) }
        if (!isSelected) {
            TopicsRepository.getTopics(channelId).subscribe({ resource ->
                if (resource.data != null && resource.data.isNotEmpty()) {
                    val topics = resource.data
                    val index = itemsList.indexOfFirst { it is ListChannel && it.id == channelId }
                    val channel = itemsList[index] as ListChannel
                    itemsList.removeAt(index)
                    itemsList.add(
                        index, ListChannel(
                            id = channel.id,
                            name = channel.name,
                            isSelected = true
                        )
                    )
                    val topicsList = topics.map { topic ->
                        ListTopic(
                            name = topic.name,
                            lastMessageId = topic.lastMessageId
                        )
                    }.reversed()
                    itemsList.addAll(index + 1, topicsList)
                    _allData.postValue(itemsList)
                    _loadingStatus.postValue(ResourceStatus.SUCCESS)
                } else {
                    resource.error?.printStackTrace()
                    //_loadingStatus.postValue(ResourceStatus.ERROR)
                }
            }, {
                it.printStackTrace()
                //_loadingStatus.postValue(ResourceStatus.ERROR)
            })
        } else {
            _allData.postValue(itemsList)
        }
    }

    fun filterData(filter: String) {
        if (allData.value != null) {
            Single.just(channelsList).toObservable()
                .distinctUntilChanged()
                .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
                .map { list ->
                    list.filter { channel ->
                        channel.name.lowercase().contains(filter.trim().lowercase())
                    }
                }.subscribe({
                    _allData.postValue(it)
                    _loadingStatus.postValue(ResourceStatus.SUCCESS)
                }, {
                    it.printStackTrace()
                    _loadingStatus.postValue(ResourceStatus.ERROR)
                })
        }
    }

}