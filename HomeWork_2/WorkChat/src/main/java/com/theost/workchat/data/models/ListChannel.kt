package com.theost.workchat.data.models

import com.theost.workchat.ui.widgets.DelegateItem

class ListChannel(val id: Int, val name: String, val topics: List<ListTopic>) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = name
}