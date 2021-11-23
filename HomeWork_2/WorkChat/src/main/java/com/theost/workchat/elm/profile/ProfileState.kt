package com.theost.workchat.elm.profile

import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListUser

data class ProfileState(
    val status: ResourceStatus = ResourceStatus.LOADING,
    val profile: ListUser? = null
)
