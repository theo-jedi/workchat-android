package com.theost.workchat.data.models.ui

import com.theost.workchat.ui.interfaces.DelegateItem

class ListLoader : DelegateItem {
    override fun id(): Any = "pagination_loader"
    override fun content(): Any ="pagination_loader"
}