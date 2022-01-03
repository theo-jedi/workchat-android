package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

class ListDate(val date: String) : DelegateItem {
    override fun id(): Any = date
    override fun content(): Any = date
}