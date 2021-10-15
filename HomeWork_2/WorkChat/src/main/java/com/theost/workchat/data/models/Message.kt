package com.theost.workchat.data.models

import java.util.*

data class Message(
    val id: Int,
    val userId: Int,
    val dialogId: Int,
    val text: String,
    val date: Date
)
