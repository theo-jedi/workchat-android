package com.theost.workchat.elm.reactions

import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListReaction

data class ReactionsState(
    val status: ResourceStatus = ResourceStatus.LOADING,
    val reactions: List<ListReaction> = emptyList()
)
