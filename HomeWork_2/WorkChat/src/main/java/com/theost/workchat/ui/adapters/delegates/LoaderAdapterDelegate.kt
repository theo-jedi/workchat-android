package com.theost.workchat.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.data.models.ui.ListLoader
import com.theost.workchat.databinding.ItemLoaderBinding
import com.theost.workchat.ui.interfaces.AdapterDelegate

class LoaderAdapterDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemLoaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {}

    override fun isOfViewType(item: Any): Boolean = item is ListLoader

    class ViewHolder(progressBar: View) : RecyclerView.ViewHolder(progressBar)
}