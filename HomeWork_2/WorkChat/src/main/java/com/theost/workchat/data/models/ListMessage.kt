package com.theost.workchat.data.models

import com.theost.workchat.ui.widgets.DelegateItem

data class ListMessage(
    val id: Int,
    val avatar: Int?,
    val username: String,
    val message: String,
    val time: String,
    val reactions: List<ListMessageReaction>,
    val messageType: MessageType
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = reactions
}