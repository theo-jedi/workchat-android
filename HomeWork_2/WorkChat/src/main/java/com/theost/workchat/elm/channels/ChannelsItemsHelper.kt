package com.theost.workchat.elm.channels

import com.theost.workchat.data.models.ui.ListChannel
import com.theost.workchat.data.models.ui.ListTopic
import com.theost.workchat.ui.interfaces.DelegateItem

object ChannelsItemsHelper {

    fun mapToListItems(channels: List<ListChannel>, topics: List<ListTopic>, selectedChannelId: Int) : List<DelegateItem> {
        val items = channels.map { channel ->
            ListChannel(
                id = channel.id,
                name = channel.name,
                isSelected = channel.id == selectedChannelId
            )
        }.sortedBy { channel -> channel.name }.toMutableList<DelegateItem>()

        val index = channels.indexOfFirst { channel -> channel.id == selectedChannelId }
        if (index != -1) items.addAll(index + 1, topics)

        return items
    }

}