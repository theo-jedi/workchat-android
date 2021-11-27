package com.theost.workchat.elm.profile

import vivid.money.elmslie.core.ElmStoreCompat

object ProfileStore {

    fun getStore(initialState: ProfileState) = ElmStoreCompat(
        initialState = initialState,
        reducer = ProfileReducer(),
        actor = ProfileActor()
    )

}
