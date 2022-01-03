package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

class ListTopic(val uid: String, val name: String, val channelId: Int, val position: Int) : DelegateItem {
    override fun id(): Any = uid
    override fun content(): Any = name
}