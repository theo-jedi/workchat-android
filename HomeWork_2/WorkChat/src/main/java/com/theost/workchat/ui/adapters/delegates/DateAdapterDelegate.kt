package com.theost.workchat.ui.adapters.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.data.models.ui.ListDate
import com.theost.workchat.ui.interfaces.AdapterDelegate
import com.theost.workchat.ui.views.DateView

class DateAdapterDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(DateView(parent.context))
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