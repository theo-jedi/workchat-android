package com.theost.workchat.elm.profile

import vivid.money.elmslie.core.ElmStoreCompat

class ProfileStore {

    private val store by lazy {
        ElmStoreCompat(
            initialState = ProfileState(),
            reducer = ProfileReducer(),
            actor = ProfileActor()
        )
    }

    fun provide() = store
}
