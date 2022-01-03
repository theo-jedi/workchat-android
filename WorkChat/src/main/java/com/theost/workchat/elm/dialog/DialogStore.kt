package com.theost.workchat.elm.dialog

import vivid.money.elmslie.core.ElmStoreCompat

object DialogStore {

    fun getStore(actor: DialogActor, initialState: DialogState) = ElmStoreCompat(
        initialState = initialState,
        reducer = DialogReducer(),
        actor = actor
    )

}
