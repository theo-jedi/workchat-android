package com.theost.workchat.data.models.core

import java.util.*

data class Message(
    val id: Int,
    val content: String,
    val date: Date,
    val senderId: Int,
    val senderName: String,
    val senderAvatarUrl: String,
    val reactions: List<Reaction>
)
