package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

class ListDate(val position: Int, val date: String) : DelegateItem {
    override fun id(): Any = position
    override fun content(): Any = date
}