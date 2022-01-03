package com.theost.workchat.elm.people

import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListUser

data class PeopleState(
    val status: ResourceStatus = ResourceStatus.LOADING,
    val people: List<ListUser> = emptyList(),
    val searchedPeople: List<ListUser> = emptyList(),
    val currentUserId: Int = -1,
    val isSearchEnabled: Boolean = false
)
