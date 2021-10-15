package com.theost.workchat.ui.widgets

data class ListReaction(val id: Int, val emoji: String) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = emoji
}