package com.theost.workchat.elm.creation.topic

import vivid.money.elmslie.core.ElmStoreCompat

object CreationTopicStore {

    fun getStore(actor: CreationTopicActor, initialState: CreationTopicState) = ElmStoreCompat(
        initialState = initialState,
        reducer = CreationTopicReducer(),
        actor = actor
    )

}
