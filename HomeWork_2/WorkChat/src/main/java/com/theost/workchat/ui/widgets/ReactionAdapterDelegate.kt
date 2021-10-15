package com.theost.workchat.ui.widgets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.databinding.ItemReactionBinding

class ReactionAdapterDelegate(private val clickListener: (reaction: ListReaction) -> Unit) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemReactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListReaction)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListReaction

    class ViewHolder(
        private val binding: ItemReactionBinding,
        private val clickListener: (reaction: ListReaction) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: ListReaction) {
            binding.emoji.text = listItem.emoji
            binding.emoji.setOnClickListener {
                clickListener(listItem)
            }
        }
    }
}