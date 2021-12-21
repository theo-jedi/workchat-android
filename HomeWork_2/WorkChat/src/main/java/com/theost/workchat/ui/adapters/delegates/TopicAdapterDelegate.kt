package com.theost.workchat.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.R
import com.theost.workchat.data.models.ui.ListTopic
import com.theost.workchat.databinding.ItemTopicBinding
import com.theost.workchat.ui.interfaces.AdapterDelegate
import com.theost.workchat.ui.interfaces.DelegateItem

class TopicAdapterDelegate(private val clickListener: (topicName: String) -> Unit) : AdapterDelegate {

    private var channelId = -1
    private var firstTopicUid = ""
    private var topicPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val backgrounds = mutableListOf<Int>()
        parent.context.resources.obtainTypedArray(R.array.topic_backgrounds).let { typedArray ->
            (0 until typedArray.length()).forEach { item ->
                backgrounds.add(typedArray.getResourceId(item, 0))
            }
            typedArray.recycle()
        }
        return ViewHolder(binding, backgrounds, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem, position: Int) {
        val listItem = (item as ListTopic)
        val topicChannelId = listItem.channelId
        val topicUid = listItem.uid

        if (topicChannelId != channelId) {
            channelId = topicChannelId
            firstTopicUid = topicUid
            topicPosition = 0
        } else {
            if (topicUid == firstTopicUid) topicPosition = -1
            topicPosition += 1
        }

        (holder as ViewHolder).bind(listItem, topicPosition)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ListTopic

    class ViewHolder(
        private val binding: ItemTopicBinding,
        private val backgrounds: List<Int>,
        private val clickListener: (topicName: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: ListTopic, topicPosition: Int) {
            binding.topicName.text = listItem.name
            binding.root.setOnClickListener { clickListener(listItem.name) }
            binding.root.setBackgroundResource(backgrounds[topicPosition % backgrounds.size])
        }

    }

}