package com.theost.workchat.ui.adapters.core

import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.ui.adapters.callbacks.PaginationAdapterHelper

open class PaginationAdapter(private val paginationAdapterHelper: PaginationAdapterHelper) : BaseAdapter() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegates[getItemViewType(position)].onBindViewHolder(holder, getItem(position), position)
        paginationAdapterHelper.onBindViewHolder(position, itemCount)
    }
}