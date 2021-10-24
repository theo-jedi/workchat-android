package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.Channel

/* Todo RxJava */
object ChannelsRepository {

    private val channels = mutableListOf(
        Channel(0, "#general", listOf(0, 1)),
        Channel(1, "#development", listOf(2, 3)),
        Channel(2, "#hr", listOf(4, 5))
    )

    fun getChannels(): List<Channel> {
        return channels
    }

}