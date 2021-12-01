package com.theost.workchat.elm.messenger

import com.theost.workchat.data.repositories.UsersRepository
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class MessengerActor(
    private val usersRepository: UsersRepository
) : ActorCompat<MessengerCommand, MessengerEvent> {
    override fun execute(command: MessengerCommand): Observable<MessengerEvent> = when (command) {
        MessengerCommand.LoadUser -> usersRepository.getUser().map { result ->
            result.fold(
                { user -> MessengerEvent.Internal.DataLoadingSuccess(user.id) },
                { MessengerEvent.Internal.DataLoadingError(it) }
            )
        }
    }
}