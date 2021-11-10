package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.core.User
import com.theost.workchat.data.models.dto.mapToStatus
import com.theost.workchat.data.models.dto.mapToUser
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.network.RetrofitHelper
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object UsersRepository {

    private val service = RetrofitHelper.retrofitService

    fun getUsers(): Single<RxResource<List<User>>> {
        return service.getUsers()
            .map { RxResource.success(it.users.map { user -> user.mapToUser() }) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    // todo Room
                }
            }.subscribeOn(Schedulers.io())
    }

    fun getUser(id: Int = -1): Single<RxResource<User>> {
        return if (id < 0) {
            service.getCurrentUser()
                .map { RxResource.success(it.mapToUser()) }
                .onErrorReturn { RxResource.error(it, null) }
                .doOnSuccess {
                    if (it.data != null) {
                        // todo Room
                    }
                }.subscribeOn(Schedulers.io())
        } else {
            service.getUser(id)
                .map { RxResource.success(it.user.mapToUser()) }
                .onErrorReturn { RxResource.error(it, null) }
                .doOnSuccess {
                    if (it.data != null) {
                        // todo Room
                    }
                }.subscribeOn(Schedulers.io())
        }
    }

    fun getUserPresence(id: Int) : Single<RxResource<UserStatus>> {
        return service.getUserPresence(id)
            .map { RxResource.success(it.presence.client.mapToStatus()) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    // todo Room
                }
            }.subscribeOn(Schedulers.io())
    }

}