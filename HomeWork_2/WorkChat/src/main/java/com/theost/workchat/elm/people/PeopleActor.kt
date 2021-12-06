package com.theost.workchat.elm.people

import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.utils.StringUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import vivid.money.elmslie.core.ActorCompat
import java.util.concurrent.TimeUnit

class PeopleActor(private val usersRepository: UsersRepository) :
    ActorCompat<PeopleCommand, PeopleEvent> {
    override fun execute(command: PeopleCommand): Observable<PeopleEvent> = when (command) {
        is PeopleCommand.LoadPeople -> {

            usersRepository.getUsers().concatMap { usersResult ->
                usersResult.fold({ resultUsers ->
                    Observable.fromIterable(
                        resultUsers.filterNot { user -> user.isBot || user.id == command.currentUserId }
                            .sortedBy { user -> user.name }
                    ).concatMap { resultUser ->
                        Observable.zip(
                            usersRepository.getUserPresence(resultUser.id),
                            Observable.just(resultUser)
                        ) { presenceResult, user ->
                            presenceResult.fold({ status ->
                                ListUser(
                                    id = user.id,
                                    name = user.name,
                                    about = user.about,
                                    avatarUrl = user.avatarUrl,
                                    status = status
                                )
                            }, {
                                ListUser(
                                    id = user.id,
                                    name = user.name,
                                    about = user.about,
                                    avatarUrl = user.avatarUrl,
                                    status = UserStatus.OFFLINE
                                )
                            })
                        }
                    }.toList().mapSuccessEvent { users -> PeopleEvent.Internal.PeopleLoadingSuccess(users) }
                }, { error -> Observable.just(PeopleEvent.Internal.DataLoadingError(error)) })
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