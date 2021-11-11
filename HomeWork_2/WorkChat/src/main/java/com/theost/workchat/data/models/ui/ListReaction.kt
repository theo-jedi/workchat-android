package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

data class ListReaction(
    val name: String,
    val code: String,
    val type: String,
    val emoji: String
) : DelegateItem {
    override fun id(): Any = name
    override fun content(): Any = emoji
}