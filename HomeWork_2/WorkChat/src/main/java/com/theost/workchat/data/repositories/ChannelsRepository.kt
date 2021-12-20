package com.theost.workchat.data.repositories

import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.core.Channel
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.database.entities.mapToChannel
import com.theost.workchat.database.entities.mapToChannelEntity
import com.theost.workchat.network.api.RetrofitHelper
import com.theost.workchat.network.dto.mapToChannel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object ChannelsRepository {

    private val service = RetrofitHelper.retrofitService

    fun getChannels(channelsType: ChannelsType, subscribedChannels: List<Int>): Observable<RxResource<List<Channel>>> {
        return Observable.concat(
            getChannelsFromCache(channelsType, subscribedChannels).toObservable(),
            getChannelsFromServer(channelsType).toObservable()
        )
    }

    private fun getChannelsFromServer(channelsType: ChannelsType): Single<RxResource<List<Channel>>> {
        return if (channelsType == ChannelsType.SUBSCRIBED) {
            service.getSubscribedChannels()
                .map { RxResource.success(it.channels.map { channel -> channel.mapToChannel() }) }
                .onErrorReturn { RxResource.error(it, null) }
                .doOnSuccess {
                    if (it.data != null) addChannelsToDatabase(it.data)
                }.subscribeOn(Schedulers.io())
        } else {
            service.getChannels()
                .map { RxResource.success(it.channels.map { channel -> channel.mapToChannel() }) }
                .onErrorReturn { RxResource.error(it, null) }
                .doOnSuccess {
                    if (it.data != null) addChannelsToDatabase(it.data)
                }.subscribeOn(Schedulers.io())
        }
    }

    private fun getChannelsFromCache(
        channelsType: ChannelsType,
        subscribedChannels: List<Int>
    ): Single<RxResource<List<Channel>>> {
        return if (channelsType == ChannelsType.SUBSCRIBED) {
            WorkChatApp.cacheDatabase.channelsDao().getAll()
                .map {
                    RxResource.success(
                        it.filter { channel ->
                            subscribedChannels.contains(channel.id)
                        }.map { channel -> channel.mapToChannel() }
                    )
                }
                .onErrorReturn { RxResource.error(it, null) }
                .subscribeOn(Schedulers.io())
        } else {
            WorkChatApp.cacheDatabase.channelsDao().getAll()
                .map { RxResource.success(it.map { channel -> channel.mapToChannel() }) }
                .onErrorReturn { RxResource.error(it, null) }
                .subscribeOn(Schedulers.io())
        }
    }

    private fun addChannelsToDatabase(channels: List<Channel>) {
        WorkChatApp.cacheDatabase.channelsDao()
            .insertAll(channels.map { it.mapToChannelEntity() })
            .subscribeOn(Schedulers.io()).subscribe()
    }

}