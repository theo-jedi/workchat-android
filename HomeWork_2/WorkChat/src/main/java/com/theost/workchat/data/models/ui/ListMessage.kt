package com.theost.workchat.data.models.ui

import android.text.Spanned
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.ui.interfaces.DelegateItem
import java.util.*

data class ListMessage(
    val id: Int,
    val content: Spanned,
    val htmlContent: String,
    val time: String,
    val date: Date,
    val senderName: String,
    val senderAvatarUrl: String,
    val reactions: List<ListMessageReaction>,
    val messageType: MessageType
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = content.toString() + reactions.toString()
}