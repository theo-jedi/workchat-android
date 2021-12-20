package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

class ListChannel(
    val id: Int,
    val name: String,
    val isSelected: Boolean
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = isSelected
}