package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.User
import com.theost.workchat.data.models.state.CacheStatus
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.state.UsersType
import com.theost.workchat.database.db.CacheDatabase
import com.theost.workchat.database.entities.mapToPresence
import com.theost.workchat.database.entities.mapToPresenceEntity
import com.theost.workchat.database.entities.mapToUser
import com.theost.workchat.database.entities.mapToUserEntity
import com.theost.workchat.network.api.Api
import com.theost.workchat.network.dto.mapToStatus
import com.theost.workchat.network.dto.mapToUser
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class UsersRepository(private val service: Api, database: CacheDatabase) {

    private var cacheStatus: CacheStatus = CacheStatus.NOT_UPDATED

    private val usersDao = database.usersDao()
    private val presenceDao = database.presenceDao()

    fun getUsers(): Observable<Result<List<User>>> {
        return if (cacheStatus == CacheStatus.UPDATED) {
            getUsersFromCache().toObservable()
        } else {
            return Observable.concat(
                getUsersFromCache().toObservable(),
                getUsersFromServer().toObservable()
            )
        }
    }

    private fun getUsersFromServer(): Single<Result<List<User>>> {
        return service.getUsers()
            .map { response ->
                Result.success(response.users
                    .filter { userDto -> userDto.isActive }
                    .map { userDto -> userDto.mapToUser() })
            }
            .onErrorReturn { Result.failure(it) }
            .flatMap { result ->
                val users = result.getOrNull()
                if (users != null) {
                    addUsersToDatabase(users, UsersType.ALL)
                        .andThen(Single.just(result))
                } else {
                    Single.just(result)
                }
            }
    }

    private fun getUsersFromCache(): Single<Result<List<User>>> {
        return usersDao.getAll()
            .map { users -> Result.success(users.map { userEntity -> userEntity.mapToUser() }) }
            .onErrorReturn { Result.failure(it) }
            .subscribeOn(Schedulers.io())
    }

    fun getUserFromServer(id: Int = -1): Single<Result<User>> {
        return if (id < 0) {
            service.getCurrentUser()
                .map { userDto -> Result.success(userDto.mapToUser()) }
                .onErrorReturn { Result.failure(it) }
                .flatMap { result ->
                    val user = result.getOrNull()
                    if (user != null) {
                        addUsersToDatabase(listOf(user), UsersType.SINGLE)
                            .andThen(Single.just(result))
                    } else {
                        Single.just(result)
                    }
                }
        } else {
            service.getUser(id)
                .map { response -> Result.success(response.user.mapToUser()) }
                .onErrorReturn { Result.failure(it) }
                .flatMap { result ->
                    val user = result.getOrNull()
                    if (user != null) {
                        addUsersToDatabase(listOf(user), UsersType.SINGLE)
                            .andThen(Single.just(result))
                    } else {
                        Single.just(result)
                    }
                }
        }
    }

    fun getUserFromCache(id: Int = -1): Single<Result<User>> {
        return usersDao.getUser(id)
            .map { userEntity -> Result.success(userEntity.mapToUser()) }
            .onErrorReturn { Result.failure(it) }
            .subscribeOn(Schedulers.io())
    }

    fun getUserPresence(userId: Int): Observable<Result<UserStatus>> {
        return Observable.concat(
            getUserPresenceFromCache(userId).toObservable(),
            getUserPresenceFromServer(userId).toObservable()
        )
    }

    fun getUserPresenceFromServer(userId: Int): Single<Result<UserStatus>> {
        return service.getUserPresence(userId)
            .map { response -> Result.success(response.presence.client.mapToStatus()) }
            .onErrorReturn { Result.failure(it) }
            .flatMap { result ->
                val status = result.getOrNull()
                if (status != null && status != UserStatus.OFFLINE) {
                    addPresenceToDatabase(userId, status)
                        .andThen(Single.just(result))
                } else {
                    removePresenceFromDatabase(userId)
                        .andThen(Single.just(result))
                }
            }
            .subscribeOn(Schedulers.io())
    }

    fun getUserPresenceFromCache(userId: Int): Single<Result<UserStatus>> {
        return presenceDao.getPresence(userId)
            .map { presenceEntity -> Result.success(presenceEntity.mapToPresence()) }
            .onErrorReturn { Result.failure(it) }
            .subscribeOn(Schedulers.io())
    }

    private fun addUsersToDatabase(users: List<User>, usersType: UsersType): Completable {
        return usersDao.insertAll(users.map { user -> user.mapToUserEntity() })
            .doOnComplete {
                if (usersType == UsersType.ALL && users.isNotEmpty()) {
                    cacheStatus = CacheStatus.UPDATED
                }
            }
    }

    private fun addPresenceToDatabase(userId: Int, status: UserStatus): Completable {
        return presenceDao.insertAll(listOf(status.mapToPresenceEntity(userId)))
    }

    private fun removePresenceFromDatabase(userId: Int): Completable {
        return presenceDao.deletePresence(userId)
    }
}