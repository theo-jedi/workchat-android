package com.theost.workchat.elm.reactions

import com.theost.workchat.data.models.ui.ListReaction
import com.theost.workchat.data.repositories.ReactionsRepository
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class ReactionsActor : ActorCompat<ReactionsCommand, ReactionsEvent> {
    override fun execute(command: ReactionsCommand): Observable<ReactionsEvent> = when (command) {
        ReactionsCommand.LoadReactions -> {
            ReactionsRepository.getReactions().map { list ->
                list.map { reaction ->
                    ListReaction(
                        name = reaction.name,
                        code = reaction.code,
                        type = reaction.type,
                        emoji = reaction.emoji
                    )
                }
            }.mapEvents(
                { reactions -> ReactionsEvent.Internal.ReactionsLoadingSuccess(reactions) },
                { error -> ReactionsEvent.Internal.DataLoadingError(error) }
            )
        }
    }
}