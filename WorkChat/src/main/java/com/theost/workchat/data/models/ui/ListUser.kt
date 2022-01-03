package com.theost.workchat.data.models.ui

import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.ui.interfaces.DelegateItem

class ListUser(
    val id: Int,
    val name: String,
    val about: String,
    val avatarUrl: String,
    val status: UserStatus
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = status
}