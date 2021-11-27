package com.theost.workchat.elm.people

import vivid.money.elmslie.core.ElmStoreCompat

object PeopleStore {

    fun getStore(initialState: PeopleState) = ElmStoreCompat(
        initialState = initialState,
        reducer = PeopleReducer(),
        actor = PeopleActor()
    )

}
