package com.theost.workchat.di.ui

import com.theost.workchat.data.repositories.ChannelsRepository
import com.theost.workchat.data.repositories.TopicsRepository
import com.theost.workchat.di.base.AppComponent
import com.theost.workchat.di.base.ScreenScope
import com.theost.workchat.elm.channels.ChannelsActor
import com.theost.workchat.ui.fragments.ChannelsFragment
import dagger.Component
import dagger.Module
import dagger.Provides

@ScreenScope
@Component(dependencies = [AppComponent::class], modules = [ChannelsModule::class])
interface ChannelsComponent {
    fun inject(fragment: ChannelsFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): ChannelsComponent
    }
}

@Module
class ChannelsModule {
    @Provides
    fun provideChannelsActor(
        channelsRepository: ChannelsRepository,
        topicsRepository: TopicsRepository
    ): ChannelsActor {
        return ChannelsActor(channelsRepository, topicsRepository)
    }
}