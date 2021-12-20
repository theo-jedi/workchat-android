package com.theost.workchat.data.models.ui

import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.ui.interfaces.DelegateItem

data class ListPhoto(
    val id: Int,
    val url: String,
    val messageType: MessageType
) : DelegateItem {
    override fun id(): Any = "photo-$id"
    override fun content(): Any = url
}