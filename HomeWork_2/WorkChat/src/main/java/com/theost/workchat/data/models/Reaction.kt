package com.theost.workchat.data.models

data class Reaction(
    val id: Int,
    val messageId: Int,
    val emoji: String,
    val userIds: List<Int>
)
