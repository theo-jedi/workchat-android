package com.theost.workchat.data.models.core

import com.theost.workchat.data.models.state.MessageType
import java.util.*

data class Message(
    val id: Int,
    val name: String,
    val text: String,
    val avatarUrl: String,
    val time: Date,
    val reactions: List<String>,
    val messageType: MessageType
)
