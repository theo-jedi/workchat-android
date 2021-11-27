package com.theost.workchat.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.R
import com.theost.workchat.data.models.ui.ListChannel
import com.theost.workchat.databinding.ItemChannelBinding
import com.theost.workchat.ui.interfaces.AdapterDelegate


class ChannelAdapterDelegate(
    private val clickListener: (channelId: Int, channelName: String, isSelected: Boolean) -> Unit
) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListChannel)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListChannel

    class ViewHolder(
        private val binding: ItemChannelBinding,
        private val clickListener: (channelId: Int, channelName: String, isSelected: Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: ListChannel) {
            binding.channelName.text = listItem.name
            binding.root.isSelected = listItem.isSelected
            if (listItem.isSelected) {
                binding.expandArrow.setImageResource(R.drawable.ic_collapse)
            } else {
                binding.expandArrow.setImageResource(R.drawable.ic_expand)
            }
            binding.root.setOnClickListener {
                clickListener(listItem.id, listItem.name, listItem.isSelected)
            }
        }

    }

}