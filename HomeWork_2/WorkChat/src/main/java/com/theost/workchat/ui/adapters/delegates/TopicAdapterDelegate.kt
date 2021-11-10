package com.theost.workchat.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.R
import com.theost.workchat.data.models.ui.ListTopic
import com.theost.workchat.databinding.ItemTopicBinding
import com.theost.workchat.ui.interfaces.AdapterDelegate

class TopicAdapterDelegate(private val clickListener: (topicName: String) -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        val backgrounds = mutableListOf<Int>()
        parent.context.resources.obtainTypedArray(R.array.topic_backgrounds).let { typedArray ->
            (0 until typedArray.length()).forEach { item ->
                backgrounds.add(typedArray.getResourceId(item, 0))
            }
            typedArray.recycle()
        }
        return ViewHolder(binding, backgrounds, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListTopic)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListTopic

    class ViewHolder(
        private val binding: ItemTopicBinding,
        private val backgrounds: List<Int>,
        private val clickListener: (topicName: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: ListTopic) {
            binding.topicName.text = listItem.name
            binding.topicCount.text = listItem.lastMessageId.toString()
            binding.root.setOnClickListener { clickListener(listItem.name) }
            binding.root.setBackgroundResource(backgrounds[adapterPosition % backgrounds.size])
        }

    }

}