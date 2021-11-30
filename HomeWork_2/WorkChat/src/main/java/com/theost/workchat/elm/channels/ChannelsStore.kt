package com.theost.workchat.elm.channels

import vivid.money.elmslie.core.ElmStoreCompat

object ChannelsStore {

    fun getStore(actor: ChannelsActor, initialState: ChannelsState) = ElmStoreCompat(
        initialState = initialState,
        reducer = ChannelsReducer(),
        actor = actor
    )

}
