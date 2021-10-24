package com.theost.workchat.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.Channel
import com.theost.workchat.data.models.ChannelsType
import com.theost.workchat.data.models.ListChannel
import com.theost.workchat.data.models.ListTopic
import com.theost.workchat.data.repositories.ChannelsRepository
import com.theost.workchat.data.repositories.TopicsRepository
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.ui.widgets.DelegateItem

class ChannelsViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<DelegateItem>>()
    val allData: LiveData<List<DelegateItem>> = _allData

    private var channelsList = listOf<ListChannel>()

    fun loadData(userId: Int, channelsType: ChannelsType) {
        var channels = ChannelsRepository.getChannels()
        val topics = TopicsRepository.getTopics()
        if (channelsType == ChannelsType.SUBSCRIBED) {
            val channelsSubscribedList = UsersRepository.getUser(userId)?.channelsIds
            if (channelsSubscribedList != null) {
                channels = channels.filter { channel ->
                    channelsSubscribedList.contains(channel.id)
                }
            }
        }
        channelsList = channels.map { channel ->
            ListChannel(
                id = channel.id,
                name = channel.name,
                topics = topics.filter { it.channelId == channel.id }.map { topic ->
                    ListTopic(id = topic.id, name = topic.name, topic.count)
                })
        }
        _allData.postValue(channelsList)
    }

    fun filterData(filter: String) {
        val list = channelsList.filter { channel ->
            channel.name.lowercase().contains(filter.trim().lowercase())
        }
        _allData.postValue(list)
    }

}