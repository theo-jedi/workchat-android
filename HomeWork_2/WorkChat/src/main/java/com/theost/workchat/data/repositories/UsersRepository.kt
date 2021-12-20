package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.User
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.database.db.CacheDatabase
import com.theost.workchat.database.entities.mapToUser
import com.theost.workchat.database.entities.mapToUserEntity
import com.theost.workchat.network.api.Api
import com.theost.workchat.network.dto.mapToStatus
import com.theost.workchat.network.dto.mapToUser
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class UsersRepository(private val service: Api, database: CacheDatabase) {

    private val usersDao = database.usersDao()

    fun getUsers(): Observable<Result<List<User>>> {
        return Observable.concat(
            getUsersFromCache().toObservable(),
            getUsersFromServer().toObservable()
        )
    }

    private fun getUsersFromServer(): Single<Result<List<User>>> {
        return service.getUsers()
            .map { response -> Result.success(response.users.map { userDto -> userDto.mapToUser() }) }
            .onErrorReturn { Result.failure(it) }
            .doOnSuccess { result ->
                if (result.isSuccess) {
                    val users = result.getOrNull()
                    if (users != null) addUsersToDatabase(users)
                }
            }
            .subscribeOn(Schedulers.io())
    }

    private fun getUsersFromCache(): Single<Result<List<User>>> {
        return usersDao.getAll()
            .map { users -> Result.success(users.map { userEntity -> userEntity.mapToUser() }) }
            .onErrorReturn { Result.failure(it) }
            .subscribeOn(Schedulers.io())
    }

    fun getUser(id: Int = -1): Observable<Result<User>> {
        return if (id < 0) {
            getUserFromServer(id).toObservable()
        } else {
            return Observable.concat(
                getUserFromCache(id).toObservable(),
                getUserFromServer(id).toObservable()
            )
        }
    }

    private fun getUserFromServer(id: Int): Single<Result<User>> {
        return if (id < 0) {
            service.getCurrentUser()
                .map { userDto -> Result.success(userDto.mapToUser()) }
                .onErrorReturn { Result.failure(it) }
                .doOnSuccess { result ->
                    if (result.isSuccess) {
                        val user = result.getOrNull()
                        if (user != null) addUsersToDatabase(listOf(user))
                    }
                }
                .subscribeOn(Schedulers.io())
        } else {
            service.getUser(id)
                .map { response -> Result.success(response.user.mapToUser()) }
                .onErrorReturn { Result.failure(it) }
                .doOnSuccess { result ->
                    if (result.isSuccess) {
                        val user = result.getOrNull()
                        if (user != null) addUsersToDatabase(listOf(user))
                    }
                }
                .subscribeOn(Schedulers.io())
        }
    }

    private fun getUserFromCache(id: Int): Single<Result<User>> {
        return usersDao.getUser(id)
            .map { userEntity -> Result.success(userEntity.mapToUser()) }
            .onErrorReturn { Result.failure(it) }
            .subscribeOn(Schedulers.io())
    }

    fun getUserPresence(id: Int): Single<Result<UserStatus>> {
        return service.getUserPresence(id)
            .map { response -> Result.success(response.presence.client.mapToStatus()) }
            .onErrorReturn { Result.failure(it) }
            .subscribeOn(Schedulers.io())
    }

    private fun addUsersToDatabase(users: List<User>) {
        usersDao.insertAll(users.map { user -> user.mapToUserEntity() })
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

}