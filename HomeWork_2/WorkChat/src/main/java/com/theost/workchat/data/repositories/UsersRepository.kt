package com.theost.workchat.data.repositories

import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.core.User
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.database.entities.mapToUser
import com.theost.workchat.database.entities.mapToUserEntity
import com.theost.workchat.network.api.RetrofitHelper
import com.theost.workchat.network.dto.mapToStatus
import com.theost.workchat.network.dto.mapToUser
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object UsersRepository {

    private val service = RetrofitHelper.retrofitService

    fun getUsers(): Observable<List<User>> {
        return Observable.concat(
            getUsersFromCache().toObservable(),
            getUsersFromServer().toObservable()
        )
    }

    private fun getUsersFromServer(): Single<List<User>> {
        return service.getUsers()
            .map { it.users.map { user -> user.mapToUser() } }
            .doOnSuccess { users -> addUsersToDatabase(users)}
            .subscribeOn(Schedulers.io())
    }

    private fun getUsersFromCache(): Single<List<User>> {
        return WorkChatApp.cacheDatabase.usersDao().getAll()
            .map { it.map { userEntity -> userEntity.mapToUser() } }
            .subscribeOn(Schedulers.io())
    }

    private fun addUsersToDatabase(users: List<User>) {
        WorkChatApp.cacheDatabase.usersDao()
            .insertAll(users.map { it.mapToUserEntity() })
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun getUser(id: Int = -1): Observable<User> {
        return if (id < 0) {
            getUserFromServer(id).toObservable()
        } else {
            return Observable.concat(
                getUserFromCache(id).toObservable(),
                getUserFromServer(id).toObservable()
            )
        }
    }

    private fun getUserFromServer(id: Int): Single<User> {
        return if (id < 0) {
            service.getCurrentUser()
                .map { user -> user.mapToUser() }
                .doOnSuccess { user -> addUsersToDatabase(listOf(user))}
                .subscribeOn(Schedulers.io())
        } else {
            service.getUser(id)
                .map { response -> response.user.mapToUser() }
                .doOnSuccess { user -> addUsersToDatabase(listOf(user)) }
                .subscribeOn(Schedulers.io())
        }
    }

    private fun getUserFromCache(id: Int): Single<User> {
        return WorkChatApp.cacheDatabase.usersDao().getUser(id)
            .map { user -> user.mapToUser() }
            .subscribeOn(Schedulers.io())
    }

    fun getUserPresence(id: Int): Single<UserStatus> {
        return service.getUserPresence(id)
            .map { response -> response.presence.client.mapToStatus() }
            .subscribeOn(Schedulers.io())
    }

}