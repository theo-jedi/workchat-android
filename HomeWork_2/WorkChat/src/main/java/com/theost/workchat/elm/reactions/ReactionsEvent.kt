package com.theost.workchat.elm.reactions

import com.theost.workchat.data.models.ui.ListReaction

sealed class ReactionsEvent {
    sealed class Ui : ReactionsEvent() {
        object LoadReactions : Ui()
        data class ChooseReaction(val reaction: ListReaction) : Ui()
    }

    sealed class Internal : ReactionsEvent() {
        data class ReactionsLoadingSuccess(val reactions: List<ListReaction>) : Internal()
        data class DataLoadingError(val error: Throwable) : Internal()
    }
}