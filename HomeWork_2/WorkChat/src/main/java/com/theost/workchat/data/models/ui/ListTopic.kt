package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

class ListTopic(val name: String, val lastMessageId: Int) : DelegateItem {
    override fun id(): Any = name
    override fun content(): Any = lastMessageId
}