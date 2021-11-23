package com.theost.workchat.elm.reactions

import vivid.money.elmslie.core.ElmStoreCompat

class ReactionsStore {

    private val store by lazy {
        ElmStoreCompat(
            initialState = ReactionsState(),
            reducer = ReactionsReducer(),
            actor = ReactionsActor()
        )
    }

    fun provide() = store
}