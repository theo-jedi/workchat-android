package com.theost.workchat.di

import com.theost.workchat.elm.channels.ChannelsStore
import com.theost.workchat.elm.people.PeopleStore
import com.theost.workchat.elm.profile.ProfileStore
import com.theost.workchat.elm.reactions.ReactionsStore

class GlobalDI private constructor() {

    val elmChannelsStoreFactory by lazy { ChannelsStore() }
    val elmPeopleStoreFactory by lazy { PeopleStore() }
    val elmProfileStoreFactory by lazy { ProfileStore() }
    val elmReactionsStoreFactory by lazy { ReactionsStore() }

    companion object {
        lateinit var INSTANCE: GlobalDI

        fun init() {
            INSTANCE = GlobalDI()
        }
    }


}