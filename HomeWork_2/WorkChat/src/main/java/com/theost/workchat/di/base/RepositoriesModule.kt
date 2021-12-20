package com.theost.workchat.di.base

import com.theost.workchat.data.repositories.*
import com.theost.workchat.database.db.CacheDatabase
import com.theost.workchat.network.api.Api
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoriesModule {

    @Singleton
    @Provides
    fun provideChannelsRepository(service: Api, database: CacheDatabase): ChannelsRepository {
        return ChannelsRepository(service, database)
    }

    @Singleton
    @Provides
    fun provideMessagesRepository(service: Api, database: CacheDatabase): MessagesRepository {
        return MessagesRepository(service, database)
    }

    @Singleton
    @Provides
    fun provideReactionsRepository(service: Api, database: CacheDatabase): ReactionsRepository {
        return ReactionsRepository(service, database)
    }

    @Singleton
    @Provides
    fun provideTopicsRepository(service: Api, database: CacheDatabase): TopicsRepository {
        return TopicsRepository(service, database)
    }

    @Singleton
    @Provides
    fun provideUsersRepository(service: Api, database: CacheDatabase): UsersRepository {
        return UsersRepository(service, database)
    }

}