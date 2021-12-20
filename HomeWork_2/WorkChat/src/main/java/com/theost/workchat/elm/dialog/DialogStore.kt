package com.theost.workchat.elm.dialog

import vivid.money.elmslie.core.ElmStoreCompat

object DialogStore {

    fun getStore(initialState: DialogState) = ElmStoreCompat(
        initialState = initialState,
        reducer = DialogReducer(),
        actor = DialogActor()
    )

}
