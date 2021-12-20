package com.theost.workchat.data.repositories

import com.theost.workchat.R
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.core.User
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object UsersRepository {

    private val users = mutableListOf(
        User(0, "Theo Jedi", "May the force be with you", R.mipmap.sample_avatar, true, mutableListOf(0)),
        User(1, "Obi Wan", "Where is Anakin?", R.mipmap.sample_avatar, true, mutableListOf(0)),
        User(2, "General Grievous", "You Think You Can Defeat Me?", R.mipmap.sample_avatar, false, mutableListOf(0))
    )

    fun getUsers(): Single<RxResource<List<User>>> {
        return Single.just(users.toList())
            .map { RxResource.success(it) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    users.clear()
                    users.addAll(it.data)
                }
            }.subscribeOn(Schedulers.io())
    }

    fun getUser(id: Int): Single<RxResource<User>> {
        return Single.just(users.toList())
            .map { list -> RxResource.success(list.find { it.id == id }) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

}