package com.theost.workchat.ui.adapters.callbacks

import com.theost.workchat.data.repositories.MessagesRepository

class PaginationAdapterHelper(private val paginationCallback: (position: Int) -> Unit) {
    fun onBindViewHolder(adapterPosition: Int, totalItemCount: Int) {
        if (adapterPosition > totalItemCount - MessagesRepository.DIALOG_NEXT_PAGE) {
            paginationCallback(adapterPosition)
        }
    }
}