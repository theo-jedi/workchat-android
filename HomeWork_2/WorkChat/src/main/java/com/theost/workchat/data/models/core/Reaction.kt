package com.theost.workchat.data.models.core

data class Reaction(
    val id: Int,
    val date: Long,
    val dialogId: Int,
    val messageId: Int,
    val emoji: String,
    val userIds: List<Int>
)
