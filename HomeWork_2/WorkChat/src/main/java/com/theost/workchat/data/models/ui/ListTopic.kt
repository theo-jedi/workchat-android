package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

class ListTopic(val name: String, val count: Int) : DelegateItem {
    override fun id(): Any = name
    override fun content(): Any = count
}