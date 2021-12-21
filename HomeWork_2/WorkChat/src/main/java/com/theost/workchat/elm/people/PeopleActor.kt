package com.theost.workchat.elm.people

import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.utils.StringUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import vivid.money.elmslie.core.ActorCompat

class PeopleActor(private val usersRepository: UsersRepository) :
    ActorCompat<PeopleCommand, PeopleEvent> {
    override fun execute(command: PeopleCommand): Observable<PeopleEvent> = when (command) {
        is PeopleCommand.LoadPeople -> {
            usersRepository.getUsers().concatMap { usersResult ->
                usersResult.fold({ resultUsers ->
                    Observable.zip(resultUsers
                        .filterNot { user -> user.isBot || user.id == command.currentUserId }
                        .sortedBy { user -> user.name }
                        .map { user ->
                            usersRepository.getUserPresenceFromCache(user.id).toObservable()
                                .map { presenceResult ->
                                    presenceResult.fold(
                                        onSuccess = { status ->
                                            ListUser(
                                                id = user.id,
                                                name = user.name,
                                                about = user.about,
                                                avatarUrl = user.avatarUrl,
                                                status = status
                                            )
                                        },
                                        onFailure = {
                                            ListUser(
                                                id = user.id,
                                                name = user.name,
                                                about = user.about,
                                                avatarUrl = user.avatarUrl,
                                                status = UserStatus.OFFLINE
                                            )
                                        })
                                }.subscribeOn(Schedulers.io())
                        }) { users -> users.map { user -> user as ListUser }.toList() }
                        .mapSuccessEvent { users ->
                            PeopleEvent.Internal.PeopleLoadingSuccess(users)
                        }
                }, { error ->
                    Observable.just(PeopleEvent.Internal.DataLoadingError(error))
                })
            }
        }
        is PeopleCommand.LoadStatuses -> {
            Observable.fromIterable(command.people).concatMap { user ->
                usersRepository.getUserPresenceFromServer(user.id).toObservable().map { result ->
                    result.fold(onSuccess = { status ->
                        ListUser(user.id, user.name, user.about, user.avatarUrl, status)
                    }, onFailure = {
                        ListUser(user.id, user.name, user.about, user.avatarUrl, UserStatus.OFFLINE)
                    })
                }
            }.toList().mapSuccessEvent { people ->
                PeopleEvent.Internal.StatusesLoadingSuccess(people)
            }
        }
        is PeopleCommand.SearchPeople -> {
            Observable.just(command.people)
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