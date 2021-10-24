package com.theost.workchat.ui.widgets

import android.animation.LayoutTransition
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.data.models.ListChannel
import com.theost.workchat.databinding.ItemChannelBinding
import com.theost.workchat.utils.DisplayUtils


class ChannelAdapterDelegate(private val clickListener: (topicId: Int) -> Unit) :
    AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemChannelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListChannel)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListChannel

    class ViewHolder(
        private val binding: ItemChannelBinding,
        private val clickListener: (topicId: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val adapter = BaseAdapter()

        fun bind(listItem: ListChannel) {
            binding.channelName.text = listItem.name
            binding.topicsList.adapter = adapter.apply {
                addDelegate(TopicAdapterDelegate() { topicId ->
                    clickListener(topicId)
                })
            }
            binding.root.setOnClickListener {
                binding.root.isSelected = !binding.root.isSelected
                if (binding.root.isSelected) {
                    DisplayUtils.animateArrowExpand(binding.expandArrow)
                    adapter.submitList(listItem.topics)
                } else {
                    DisplayUtils.animateArrowCollapse(binding.expandArrow)
                    adapter.submitList(listOf())
                }
            }
        }

    }

}