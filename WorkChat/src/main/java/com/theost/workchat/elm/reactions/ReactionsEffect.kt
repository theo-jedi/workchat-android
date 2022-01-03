package com.theost.workchat.elm.reactions

import com.theost.workchat.data.models.ui.ListReaction

sealed class ReactionsEffect {
    object ShowLoading : ReactionsEffect()
    object HideLoading : ReactionsEffect()
    data class ChooseReaction(val reaction: ListReaction) : ReactionsEffect()
}