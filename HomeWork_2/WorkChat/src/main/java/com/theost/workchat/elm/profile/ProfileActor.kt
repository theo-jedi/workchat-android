package com.theost.workchat.elm.profile

import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.data.repositories.UsersRepository
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class ProfileActor : ActorCompat<ProfileCommand, ProfileEvent> {
    override fun execute(command: ProfileCommand): Observable<ProfileEvent> = when (command) {
        is ProfileCommand.LoadProfile -> {
            UsersRepository.getUser(command.userId).map { user ->
                ListUser(
                    id = user.id,
                    name = user.name,
                    about = user.about,
                    avatarUrl = user.avatarUrl,
                    status = UserStatus.OFFLINE
                )
            }.mapEvents(
                { profile -> ProfileEvent.Internal.ProfileLoadingSuccess(profile) },
                { error -> ProfileEvent.Internal.DataLoadingError(error) }
            )
        }
    }
}