package com.theost.workchat.data.models

import com.theost.workchat.ui.widgets.DelegateItem

class ListUser(
    val id: Int,
    val name: String,
    val about: String,
    val avatar: Int,
    val status: Boolean
) : DelegateItem {
    override fun id(): Any = id

    override fun content(): Any = status
}