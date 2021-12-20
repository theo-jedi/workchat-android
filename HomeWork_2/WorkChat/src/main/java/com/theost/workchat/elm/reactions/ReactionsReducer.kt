package com.theost.workchat.elm.reactions

import android.util.Log
import com.theost.workchat.data.models.state.ResourceStatus
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class ReactionsReducer : DslReducer<ReactionsEvent, ReactionsState, ReactionsEffect, ReactionsCommand>() {
    override fun Result.reduce(event: ReactionsEvent): Any = when (event) {
        is ReactionsEvent.Internal.ReactionsLoadingSuccess -> {
            if (event.reactions.isNotEmpty()) {
                state { copy(status = ResourceStatus.SUCCESS, reactions = event.reactions) }
                effects { +ReactionsEffect.HideLoading }
            } else {
                Log.d("reactions_reducer", "Reactions list is empty")
            }
        }
        is ReactionsEvent.Internal.DataLoadingError -> {
            state { copy(status = ResourceStatus.ERROR) }
        }
        is ReactionsEvent.Ui.LoadReactions -> {
            state { copy(status = ResourceStatus.LOADING) }
            commands { +ReactionsCommand.LoadReactions }
            effects { +ReactionsEffect.ShowLoading }
        }
        is ReactionsEvent.Ui.ChooseReaction -> {
            effects { +ReactionsEffect.ChooseReaction(event.reaction) }
        }
    }
}