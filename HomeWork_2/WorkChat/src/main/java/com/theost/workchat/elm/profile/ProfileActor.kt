package com.theost.workchat.elm.profile

import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.data.repositories.UsersRepository
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class ProfileActor(private val usersRepository: UsersRepository) : ActorCompat<ProfileCommand, ProfileEvent> {
    override fun execute(command: ProfileCommand): Observable<ProfileEvent> = when (command) {
        is ProfileCommand.LoadProfile -> {
            usersRepository.getUser(command.userId).map { result ->
                result.fold({ user ->
                    ProfileEvent.Internal.ProfileLoadingSuccess(
                        ListUser(
                            id = user.id,
                            name = user.name,
                            about = user.about,
                            avatarUrl = user.avatarUrl,
                            status = UserStatus.OFFLINE
                        )
                    )
                }, { error -> ProfileEvent.Internal.DataLoadingError(error) })
            }
        }
    }
}