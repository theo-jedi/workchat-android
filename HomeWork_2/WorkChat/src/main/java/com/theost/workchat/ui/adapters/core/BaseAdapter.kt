package com.theost.workchat.ui.adapters.core

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.ui.adapters.callbacks.DelegateItemCallback
import com.theost.workchat.ui.interfaces.AdapterDelegate
import com.theost.workchat.ui.interfaces.DelegateItem

open class BaseAdapter : ListAdapter<DelegateItem, RecyclerView.ViewHolder>(DelegateItemCallback()) {
    private val delegates: MutableList<AdapterDelegate> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegates[viewType].onCreateViewHolder(parent)


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegates[getItemViewType(position)].onBindViewHolder(holder, getItem(position), position)
    }

    fun addDelegate(delegate: AdapterDelegate) {
        delegates.add(delegate)
    }

    override fun getItemViewType(position: Int): Int {
        return delegates.indexOfFirst { it.isOfViewType(currentList[position]) }
    }
}