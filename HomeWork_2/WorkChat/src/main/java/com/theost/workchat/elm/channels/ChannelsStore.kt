package com.theost.workchat.elm.channels

import vivid.money.elmslie.core.ElmStoreCompat

class ChannelsStore {

    private val store by lazy {
        ElmStoreCompat(
            initialState = ChannelsState(),
            reducer = ChannelsReducer(),
            actor = ChannelsActor()
        )
    }

    fun provide() = store
}
