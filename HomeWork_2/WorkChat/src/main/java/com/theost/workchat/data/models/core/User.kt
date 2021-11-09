package com.theost.workchat.data.models.core

data class User(
    val id: Int,
    val name: String,
    val about: String,
    val avatarUrl: String,
    val isActive: Boolean,
    val channelsIds: List<Int>
)
