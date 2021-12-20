package com.theost.workchat.elm.creation.channel

import vivid.money.elmslie.core.ElmStoreCompat

object CreationChannelStore {

    fun getStore(actor: CreationChannelActor, initialState: CreationChannelState) = ElmStoreCompat(
        initialState = initialState,
        reducer = CreationChannelReducer(),
        actor = actor
    )

}
