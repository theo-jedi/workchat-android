package com.theost.workchat.data.models

data class User(
    val id: Int,
    val name: String,
    val status: String,
    val avatar: String,
    val dialogsIds: List<Int>
)
