package com.theost.workchat.data.models

import com.theost.workchat.ui.widgets.DelegateItem

class ListDate(val date: String) : DelegateItem {
    override fun id(): Any = date
    override fun content(): Any = date
}