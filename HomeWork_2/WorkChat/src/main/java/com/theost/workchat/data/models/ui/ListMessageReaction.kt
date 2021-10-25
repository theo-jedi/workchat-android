package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

data class ListMessageReaction(
    val id: Int,
    val emoji: String,
    val count: Int,
    val isSelected: Boolean
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = emoji
}