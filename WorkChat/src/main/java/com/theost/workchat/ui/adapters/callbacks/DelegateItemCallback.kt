package com.theost.workchat.ui.adapters.callbacks

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.theost.workchat.ui.interfaces.DelegateItem

class DelegateItemCallback : DiffUtil.ItemCallback<DelegateItem>() {
    override fun areItemsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean =
        oldItem::class == newItem::class && oldItem.id() == newItem.id()

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean =
        oldItem.content() == newItem.content()
}