package com.theost.workchat.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.databinding.ItemPeopleBinding
import com.theost.workchat.ui.interfaces.AdapterDelegate

class PeopleAdapterDelegate(private val clickListener: (profileId: Int) -> Unit) :
    AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemPeopleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListUser)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListUser

    class ViewHolder(
        private val binding: ItemPeopleBinding,
        private val clickListener: (profileId: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: ListUser) {
            Glide.with(binding.root).load(listItem.avatarUrl).into(binding.userAvatar)

            // todo user status

            binding.root.setOnClickListener { clickListener(listItem.id) }
            binding.userName.text = listItem.name
            binding.userAbout.text = listItem.about
        }

    }

}