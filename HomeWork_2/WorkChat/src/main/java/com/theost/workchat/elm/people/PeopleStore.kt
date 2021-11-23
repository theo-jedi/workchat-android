package com.theost.workchat.elm.people

import vivid.money.elmslie.core.ElmStoreCompat

class PeopleStore {

    private val store by lazy {
        ElmStoreCompat(
            initialState = PeopleState(),
            reducer = PeopleReducer(),
            actor = PeopleActor()
        )
    }

    fun provide() = store
}
