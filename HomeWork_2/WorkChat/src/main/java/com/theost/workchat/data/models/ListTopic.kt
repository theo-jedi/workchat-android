package com.theost.workchat.data.models

import com.theost.workchat.ui.widgets.DelegateItem

class ListTopic(val id: Int, val name: String, val count: Int) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = name + count
}