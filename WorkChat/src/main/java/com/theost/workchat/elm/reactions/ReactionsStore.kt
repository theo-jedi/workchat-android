package com.theost.workchat.elm.reactions

import vivid.money.elmslie.core.ElmStoreCompat

object ReactionsStore {

    fun getStore(actor: ReactionsActor, initialState: ReactionsState) = ElmStoreCompat(
        initialState = initialState,
        reducer = ReactionsReducer(),
        actor = actor
    )

}