package com.theost.workchat.data.repositories

import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.core.User
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.database.entities.mapToUser
import com.theost.workchat.database.entities.mapToUserEntity
import com.theost.workchat.network.api.RetrofitHelper
import com.theost.workchat.network.dto.mapToStatus
import com.theost.workchat.network.dto.mapToUser
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object UsersRepository {

    private val service = RetrofitHelper.retrofitService

    fun getUsers(): Observable<RxResource<List<User>>> {
        return Observable.concat(
            getUsersFromCache().toObservable(),
            getUsersFromServer().toObservable()
        )
    }

    private fun getUsersFromServer(): Single<RxResource<List<User>>> {
        return service.getUsers()
            .map { RxResource.success(it.users.map { user -> user.mapToUser() }) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) addUsersToDatabase(it.data)
            }.subscribeOn(Schedulers.io())
    }

    private fun getUsersFromCache(): Single<RxResource<List<User>>> {
        return WorkChatApp.cacheDatabase.usersDao().getAll()
            .map { RxResource.success(it.map { userEntity -> userEntity.mapToUser() }) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

    private fun addUsersToDatabase(users: List<User>) {
        WorkChatApp.cacheDatabase.usersDao()
            .insertAll(users.map { it.mapToUserEntity() })
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun getUser(id: Int = -1): Observable<RxResource<User>> {
        return if (id < 0) {
            getUserFromServer(id).toObservable()
        } else {
            return Observable.concat(
                getUserFromCache(id).toObservable(),
                getUserFromServer(id).toObservable()
            )
        }
    }

    private fun getUserFromServer(id: Int): Single<RxResource<User>> {
        return if (id < 0) {
            service.getCurrentUser()
                .map { RxResource.success(it.mapToUser()) }
                .onErrorReturn { RxResource.error(it, null) }
                .doOnSuccess {
                    if (it.data != null) addUsersToDatabase(listOf(it.data))
                }.subscribeOn(Schedulers.io())
        } else {
            service.getUser(id)
                .map { RxResource.success(it.user.mapToUser()) }
                .onErrorReturn { RxResource.error(it, null) }
                .doOnSuccess {
                    if (it.data != null) addUsersToDatabase(listOf(it.data))
                }.subscribeOn(Schedulers.io())
        }
    }

    private fun getUserFromCache(id: Int): Single<RxResource<User>> {
        return WorkChatApp.cacheDatabase.usersDao().getUser(id)
            .map { RxResource.success(it.mapToUser()) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

    fun getUserPresence(id: Int): Single<RxResource<UserStatus>> {
        return service.getUserPresence(id)
            .map { RxResource.success(it.presence.client.mapToStatus()) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

}