package com.theost.workchat.data.models

import com.theost.workchat.ui.widgets.DelegateItem

data class ListReaction(val id: Int, val emoji: String) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = emoji
}