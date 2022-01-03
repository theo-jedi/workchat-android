package com.theost.workchat.elm.profile

import vivid.money.elmslie.core.ElmStoreCompat

object ProfileStore {

    fun getStore(actor: ProfileActor, initialState: ProfileState) = ElmStoreCompat(
        initialState = initialState,
        reducer = ProfileReducer(),
        actor = actor
    )

}
