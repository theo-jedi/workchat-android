package com.theost.workchat.elm.dialog

import com.theost.workchat.data.models.state.InputStatus
import com.theost.workchat.data.models.state.PaginationStatus
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.state.ScrollStatus
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.ui.interfaces.DelegateItem

data class DialogState(
    val status: ResourceStatus = ResourceStatus.LOADING,
    val paginationStatus: PaginationStatus = PaginationStatus.PARTIAL,
    val scrollStatus: ScrollStatus = ScrollStatus.STAY,
    val inputStatus: InputStatus = InputStatus.EMPTY,
    val items: List<DelegateItem> = emptyList(),
    val messages: List<ListMessage> = emptyList(),
    val channelName: String = "",
    val topicName: String = "",
    val currentUserId: Int = -1,
    val savedPosition: Int = 0
)
