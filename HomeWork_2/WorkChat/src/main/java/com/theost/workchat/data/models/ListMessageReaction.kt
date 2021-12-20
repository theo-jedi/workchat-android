package com.theost.workchat.data.models

import com.theost.workchat.ui.widgets.DelegateItem

data class ListMessageReaction(
    val id: Int,
    val emoji: String,
    val count: Int,
    val isSelected: Boolean
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = emoji
}