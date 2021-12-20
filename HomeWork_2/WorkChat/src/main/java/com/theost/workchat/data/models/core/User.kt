package com.theost.workchat.data.models.core

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val about: String,
    val avatarUrl: String
)
