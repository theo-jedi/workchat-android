package com.theost.workchat.elm.messenger

import com.theost.workchat.data.models.state.ResourceStatus

data class MessengerState(
    val status: ResourceStatus = ResourceStatus.SUCCESS,
    val currentUserId: Int = -1,
    val selectedItemId: Int = -1,
    val selectedFragment: String = ""
)