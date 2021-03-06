package com.theost.workchat.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.theost.workchat.R
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.databinding.ItemPeopleBinding
import com.theost.workchat.ui.interfaces.AdapterDelegate
import com.theost.workchat.ui.interfaces.DelegateItem

class PeopleAdapterDelegate(private val clickListener: (userId: Int) -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemPeopleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem, position: Int) {
        (holder as ViewHolder).bind(item as ListUser)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ListUser

    class ViewHolder(
        private val binding: ItemPeopleBinding,
        private val clickListener: (userId: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: ListUser) {
            Glide.with(binding.root)
                .load(listItem.avatarUrl)
                .placeholder(R.drawable.ic_avatar_loading)
                .error(R.drawable.ic_avatar_error)
                .into(binding.userAvatar)

            binding.root.setOnClickListener { clickListener(listItem.id) }
            binding.userName.text = listItem.name
            binding.userAbout.text = listItem.about
            when (listItem.status) {
                UserStatus.ONLINE -> {
                    binding.userStatusOnline.visibility = View.VISIBLE
                    binding.userStatusIdle.visibility = View.INVISIBLE
                }
                UserStatus.IDLE -> {
                    binding.userStatusIdle.visibility = View.VISIBLE
                    binding.userStatusOnline.visibility = View.INVISIBLE
                }
                else -> {
                    binding.userStatusOnline.visibility = View.INVISIBLE
                    binding.userStatusIdle.visibility = View.INVISIBLE
                }
            }
        }

    }

}