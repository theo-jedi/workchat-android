package com.theost.workchat.di.base

import android.content.Context
import com.theost.workchat.data.repositories.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class, RepositoriesModule::class])
interface AppComponent {
    fun channelsRepository(): ChannelsRepository
    fun messagesRepository(): MessagesRepository
    fun reactionsRepository(): ReactionsRepository
    fun topicsRepository(): TopicsRepository
    fun usersRepository(): UsersRepository

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): AppComponent
    }
}