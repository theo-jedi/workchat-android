package com.theost.workchat.data.models.core

data class Topic(
    val id: Int,
    val channelId: Int,
    val name: String,
    val count: Int
)
