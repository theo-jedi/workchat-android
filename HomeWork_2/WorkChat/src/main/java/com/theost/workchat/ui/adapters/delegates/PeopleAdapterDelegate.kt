package com.theost.workchat.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
            binding.root.setOnClickListener { clickListener(listItem.id) }
            binding.userName.text = listItem.name
            binding.userAbout.text = listItem.about
            binding.userStatus.visibility = if (listItem.status) View.VISIBLE else View.INVISIBLE
            binding.userAvatar.setImageResource(listItem.avatar)
        }

    }

}