package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

data class ListReaction(val id: Int, val emoji: String) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = emoji
}