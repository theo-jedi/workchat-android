package com.theost.workchat.di.ui

import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.di.base.AppComponent
import com.theost.workchat.di.base.ScreenScope
import com.theost.workchat.elm.creation.topic.CreationTopicActor
import com.theost.workchat.ui.fragments.CreationTopicFragment
import dagger.Component
import dagger.Module
import dagger.Provides

@ScreenScope
@Component(dependencies = [AppComponent::class], modules = [CreationTopicModule::class])
interface CreationTopicComponent {
    fun inject(fragment: CreationTopicFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): CreationTopicComponent
    }
}

@Module
class CreationTopicModule {
    @Provides
    fun provideChannelsActor(messagesRepository: MessagesRepository): CreationTopicActor {
        return CreationTopicActor(messagesRepository)
    }
}