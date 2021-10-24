package com.theost.workchat.ui.widgets

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.R
import com.theost.workchat.data.models.ListTopic
import com.theost.workchat.databinding.ItemTopicBinding

class TopicAdapterDelegate(private val clickListener: (topicId: Int) -> Unit) :
    AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        val backgroundColors = parent.context.resources.getStringArray(R.array.topic_backgrounds)
        return ViewHolder(binding, backgroundColors, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListTopic)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListTopic

    class ViewHolder(
        private val binding: ItemTopicBinding,
        private val backgroundColors: Array<String>,
        private val clickListener: (profileId: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: ListTopic) {
            binding.root.setOnClickListener { clickListener(listItem.id) }
            binding.root.setBackgroundColor(Color.parseColor(backgroundColors[adapterPosition % backgroundColors.size]))
            binding.topicName.text = listItem.name
            binding.topicCount.text = listItem.count.toString()
        }

    }

}