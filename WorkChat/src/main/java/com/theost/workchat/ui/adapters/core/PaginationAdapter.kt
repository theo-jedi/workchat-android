package com.theost.workchat.ui.adapters.core

import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.ui.adapters.callbacks.PaginationAdapterHelper

open class PaginationAdapter(private val paginationAdapterHelper: PaginationAdapterHelper) : BaseAdapter() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        paginationAdapterHelper.onBindViewHolder(position, itemCount)
    }
}