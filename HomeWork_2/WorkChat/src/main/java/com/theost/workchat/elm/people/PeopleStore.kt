package com.theost.workchat.elm.people

import vivid.money.elmslie.core.ElmStoreCompat

object PeopleStore {

    fun getStore(actor: PeopleActor, initialState: PeopleState) = ElmStoreCompat(
        initialState = initialState,
        reducer = PeopleReducer(),
        actor = actor
    )

}
