package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.Channel
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.database.db.CacheDatabase
import com.theost.workchat.database.entities.mapToChannel
import com.theost.workchat.database.entities.mapToChannelEntity
import com.theost.workchat.network.api.Api
import com.theost.workchat.network.dto.mapToChannel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ChannelsRepository(private val service: Api, database: CacheDatabase) {

    private val channelsDao = database.channelsDao()

    fun getChannels(
        channelsType: ChannelsType,
        subscribedChannels: List<Int>
    ): Observable<Result<List<Channel>>> {
        return Observable.concat(
            getChannelsFromCache(channelsType, subscribedChannels).toObservable(),
            getChannelsFromServer(channelsType).toObservable()
        )
    }

    private fun getChannelsFromServer(channelsType: ChannelsType): Single<Result<List<Channel>>> {
        return if (channelsType == ChannelsType.SUBSCRIBED) {
            service.getSubscribedChannels()
                .map { response -> Result.success(response.channels.map { channelDto -> channelDto.mapToChannel() }) }
                .onErrorReturn { Result.failure(it) }
                .doOnSuccess { result ->
                    if (result.isSuccess) {
                        val channels = result.getOrNull()
                        if (channels != null) addChannelsToDatabase(channels)
                    }
                }
                .subscribeOn(Schedulers.io())
        } else {
            service.getChannels()
                .map { response -> Result.success(response.channels.map { channelDto -> channelDto.mapToChannel() }) }
                .onErrorReturn { Result.failure(it) }
                .doOnSuccess { result ->
                    if (result.isSuccess) {
                        val channels = result.getOrNull()
                        if (channels != null) addChannelsToDatabase(channels)
                    }
                }
                .subscribeOn(Schedulers.io())
        }
    }

    private fun getChannelsFromCache(
        channelsType: ChannelsType,
        subscribedChannels: List<Int>
    ): Single<Result<List<Channel>>> {
        return if (channelsType == ChannelsType.SUBSCRIBED) {
            channelsDao.getAll()
                .map { channels ->
                    Result.success(channels
                        .filter { channelEntity -> subscribedChannels.contains(channelEntity.id) }
                        .map { channelEntity -> channelEntity.mapToChannel() }
                    )
                }
                .onErrorReturn { Result.failure(it) }
                .subscribeOn(Schedulers.io())
        } else {
            channelsDao.getAll()
                .map { channels -> Result.success(channels.map { channelEntity -> channelEntity.mapToChannel() }) }
                .onErrorReturn { Result.failure(it) }
                .subscribeOn(Schedulers.io())
        }
    }

    private fun addChannelsToDatabase(channels: List<Channel>) {
        channelsDao.insertAll(channels.map { channel -> channel.mapToChannelEntity() })
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

}