package com.theost.workchat.elm.reactions

import com.theost.workchat.data.models.ui.ListReaction
import com.theost.workchat.data.repositories.ReactionsRepository
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class ReactionsActor(private val reactionsRepository: ReactionsRepository) :
    ActorCompat<ReactionsCommand, ReactionsEvent> {
    override fun execute(command: ReactionsCommand): Observable<ReactionsEvent> = when (command) {
        ReactionsCommand.LoadReactions -> {
            reactionsRepository.getReactionsFromCache().toObservable()
                .switchIfEmpty { reactionsRepository.getReactionsFromServer() }
                .map { reactionsResult ->
                    reactionsResult.fold({ reactions ->
                        ReactionsEvent.Internal.ReactionsLoadingSuccess(
                            reactions.map { reaction ->
                                ListReaction(
                                    name = reaction.name,
                                    code = reaction.code,
                                    type = reaction.type,
                                    emoji = reaction.emoji
                                )
                            }
                        )
                    }, { error -> ReactionsEvent.Internal.DataLoadingError(error) })
                }
        }
    }
}