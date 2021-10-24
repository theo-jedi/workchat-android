package com.theost.workchat.data.models

data class Channel(
    val id: Int,
    val name: String,
    val topics: List<Int>
)
