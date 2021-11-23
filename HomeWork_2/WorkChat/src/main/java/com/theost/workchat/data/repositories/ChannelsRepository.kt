package com.theost.workchat.data.repositories

import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.core.Channel
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.database.entities.mapToChannel
import com.theost.workchat.database.entities.mapToChannelEntity
import com.theost.workchat.network.api.RetrofitHelper
import com.theost.workchat.network.dto.mapToChannel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object ChannelsRepository {

    private val service = RetrofitHelper.retrofitService

    fun getChannels(
        channelsType: ChannelsType,
        subscribedChannels: List<Int>
    ): Observable<List<Channel>> {
        return Observable.concat(
            getChannelsFromCache(channelsType, subscribedChannels).toObservable(),
            getChannelsFromServer(channelsType).toObservable()
        )
    }

    private fun getChannelsFromServer(channelsType: ChannelsType): Single<List<Channel>> {
        return if (channelsType == ChannelsType.SUBSCRIBED) {
            service.getSubscribedChannels()
                .map { it.channels.map { channel -> channel.mapToChannel() } }
                .doOnSuccess { channels -> addChannelsToDatabase(channels) }
                .subscribeOn(Schedulers.io())
        } else {
            service.getChannels()
                .map { it.channels.map { channel -> channel.mapToChannel() } }
                .doOnSuccess { channels -> addChannelsToDatabase(channels) }
                .subscribeOn(Schedulers.io())
        }
    }

    private fun getChannelsFromCache(
        channelsType: ChannelsType,
        subscribedChannels: List<Int>
    ): Single<List<Channel>> {
        return if (channelsType == ChannelsType.SUBSCRIBED) {
            WorkChatApp.cacheDatabase.channelsDao().getAll().map {
                it.filter { channel -> subscribedChannels.contains(channel.id) }
                    .map { channel -> channel.mapToChannel() }
            }.subscribeOn(Schedulers.io())
        } else {
            WorkChatApp.cacheDatabase.channelsDao().getAll()
                .map { it.map { channel -> channel.mapToChannel() } }
                .subscribeOn(Schedulers.io())
        }
    }

    private fun addChannelsToDatabase(channels: List<Channel>) {
        WorkChatApp.cacheDatabase.channelsDao()
            .insertAll(channels.map { it.mapToChannelEntity() })
            .subscribeOn(Schedulers.io()).subscribe()
    }

}