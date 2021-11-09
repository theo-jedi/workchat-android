package com.theost.workchat.data.models.ui

import android.text.Spanned
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.ui.interfaces.DelegateItem

data class ListMessage(
    val id: Int,
    val name: String,
    val text: Spanned,
    val avatar: String,
    val time: String,
    val reactions: List<ListMessageReaction>,
    val messageType: MessageType
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = reactions
}