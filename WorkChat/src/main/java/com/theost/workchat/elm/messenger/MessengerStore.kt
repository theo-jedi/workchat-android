package com.theost.workchat.elm.messenger

import vivid.money.elmslie.core.ElmStoreCompat

object MessengerStore {

    fun getStore(actor: MessengerActor, initialState: MessengerState) = ElmStoreCompat(
        initialState = initialState,
        reducer = MessengerReducer(),
        actor = actor
    )

}
