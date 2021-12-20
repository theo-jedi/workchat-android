package com.theost.workchat.elm.profile

import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListUser

data class ProfileState(
    val status: ResourceStatus = ResourceStatus.LOADING,
    val userId: Int = -1,
    val isCurrentUser: Boolean = false,
    val profile: ListUser? = null
)
