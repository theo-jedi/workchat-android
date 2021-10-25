package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

class ListTopic(val id: Int, val name: String, val count: Int) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = name + count
}