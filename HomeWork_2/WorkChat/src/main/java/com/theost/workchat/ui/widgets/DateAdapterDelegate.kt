package com.theost.workchat.ui.widgets

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.data.models.ListDate
import com.theost.workchat.ui.views.DateView

class DateAdapterDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val dateView = DateView(parent.context)
        return ViewHolder(dateView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListDate)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListDate

    class ViewHolder(private val dateView: DateView) : RecyclerView.ViewHolder(dateView) {

        fun bind(listDate: ListDate) {
            dateView.text = listDate.date
        }
    }
}