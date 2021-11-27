package com.theost.workchat.elm.people

import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.utils.StringUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import vivid.money.elmslie.core.ActorCompat
import java.util.concurrent.TimeUnit

class PeopleActor : ActorCompat<PeopleCommand, PeopleEvent> {
    override fun execute(command: PeopleCommand): Observable<PeopleEvent> = when (command) {
        is PeopleCommand.LoadPeople -> {
            UsersRepository.getUsers().map { result ->
                result.fold({ users ->
                    PeopleEvent.Internal.PeopleLoadingSuccess(users
                        .filterNot { user -> user.id == command.currentUserId }
                        .sortedBy { user -> user.name }
                        .map { user ->
                            ListUser(
                                id = user.id,
                                name = user.name,
                                about = user.about,
                                avatarUrl = user.avatarUrl,
                                status = UserStatus.OFFLINE
                            )
                        }
                    )
                }, { error -> PeopleEvent.Internal.DataLoadingError(error) })
            }
        }
        is PeopleCommand.SearchPeople -> {
            Observable.just(command.people)
                .distinctUntilChanged()
                .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
                .map { people ->
                    people.filter { user ->
                        StringUtils.containsQuery(user.name, command.query)
                    }
                }.mapEvents(
                    { people -> PeopleEvent.Internal.PeopleSearchingSuccess(people) },
                    { error -> PeopleEvent.Internal.DataLoadingError(error) }
                )
        }
        is PeopleCommand.RestorePeople -> {
            Observable.just(command.people).mapEvents(
                { people -> PeopleEvent.Internal.PeopleSearchingSuccess(people) },
                { error -> PeopleEvent.Internal.DataLoadingError(error) }
            )
        }
    }
}