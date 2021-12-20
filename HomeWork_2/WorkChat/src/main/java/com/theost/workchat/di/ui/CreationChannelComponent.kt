package com.theost.workchat.di.ui

import com.theost.workchat.data.repositories.ChannelsRepository
import com.theost.workchat.di.base.AppComponent
import com.theost.workchat.di.base.ScreenScope
import com.theost.workchat.elm.creation.channel.CreationChannelActor
import com.theost.workchat.ui.fragments.CreationChannelFragment
import dagger.Component
import dagger.Module
import dagger.Provides

@ScreenScope
@Component(dependencies = [AppComponent::class], modules = [CreationChannelModule::class])
interface CreationChannelComponent {
    fun inject(fragment: CreationChannelFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): CreationChannelComponent
    }
}

@Module
class CreationChannelModule {
    @Provides
    fun provideChannelsActor(channelsRepository: ChannelsRepository): CreationChannelActor {
        return CreationChannelActor(channelsRepository)
    }
}