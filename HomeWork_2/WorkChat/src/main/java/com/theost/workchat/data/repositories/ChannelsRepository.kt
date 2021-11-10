package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.Channel
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.dto.mapToChannel
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.network.RetrofitHelper
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object ChannelsRepository {

    private val service = RetrofitHelper.retrofitService

    fun getChannels(channelsType: ChannelsType): Single<RxResource<List<Channel>>> {
        if (channelsType == ChannelsType.ALL) {
            return service.getChannels()
                .map { RxResource.success(it.channels.map { channel -> channel.mapToChannel() }) }
                .onErrorReturn { RxResource.error(it, null) }
                .doOnSuccess {
                    if (it.data != null) {
                        // todo Room
                    }
                }.subscribeOn(Schedulers.io())
        } else {
            return service.getSubscribedChannels()
                .map { RxResource.success(it.channels.map { channel -> channel.mapToChannel() }) }
                .onErrorReturn { RxResource.error(it, null) }
                .doOnSuccess {
                    if (it.data != null) {
                        // todo Room
                    }
                }.subscribeOn(Schedulers.io())
        }
    }

}