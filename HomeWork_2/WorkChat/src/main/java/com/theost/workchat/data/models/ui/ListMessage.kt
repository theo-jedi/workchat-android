package com.theost.workchat.data.models.ui

import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.ui.interfaces.DelegateItem

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