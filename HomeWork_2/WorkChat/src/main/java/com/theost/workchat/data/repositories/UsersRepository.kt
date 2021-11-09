package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.core.User
import com.theost.workchat.data.models.dto.mapToUser
import com.theost.workchat.network.RetrofitHelper
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object UsersRepository {

    private val service = RetrofitHelper.retrofitService

    private val users = mutableListOf(
        User(0, "Theo Jedi", "May the force be with you", "", true, mutableListOf(0)),
        User(1, "Obi Wan", "Where is Anakin?", "", true, mutableListOf(0)),
        User(2, "General Grievous", "You Think You Can Defeat Me?", "", false, mutableListOf(0))
    )

    fun getUsers(): Single<RxResource<List<User>>> {
        return service.getUsers()
            .map { RxResource.success(it.users.map { user -> user.mapToUser() }) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    users.clear()
                    users.addAll(it.data)
                }
            }.subscribeOn(Schedulers.io())
    }

    fun getUser(id: Int): Single<RxResource<User>> {
        return if (id < 0) {
            service.getCurrentUser()
                .map { RxResource.success(it.mapToUser()) }
                .onErrorReturn { RxResource.error(it, null) }
                .subscribeOn(Schedulers.io())
        } else {
            service.getUser(id)
                .map { RxResource.success(it.user.mapToUser()) }
                .onErrorReturn { RxResource.error(it, null) }
                .subscribeOn(Schedulers.io())
        }
    }

}