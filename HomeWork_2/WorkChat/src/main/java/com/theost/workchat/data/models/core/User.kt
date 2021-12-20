package com.theost.workchat.data.models.core

data class User(
    val id: Int,
    val name: String,
    val about: String,
    val avatar: Int,
    val status: Boolean,
    val channelsIds: List<Int>
)
