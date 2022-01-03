package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

data class ListMessageReaction(
    val name: String,
    val code: String,
    val type: String,
    val emoji: String,
    val count: Int,
    val isSelected: Boolean
) : DelegateItem {
    override fun id(): Any = name
    override fun content(): Any = count
}