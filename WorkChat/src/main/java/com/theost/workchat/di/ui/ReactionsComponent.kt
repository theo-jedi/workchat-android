package com.theost.workchat.di.ui

import com.theost.workchat.data.repositories.ReactionsRepository
import com.theost.workchat.di.base.AppComponent
import com.theost.workchat.di.base.ScreenScope
import com.theost.workchat.elm.reactions.ReactionsActor
import com.theost.workchat.ui.fragments.ReactionsFragment
import dagger.Component
import dagger.Module
import dagger.Provides

@ScreenScope
@Component(dependencies = [AppComponent::class], modules = [ReactionsModule::class])
interface ReactionsComponent {
    fun inject(fragment: ReactionsFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): ReactionsComponent
    }
}

@Module
class ReactionsModule {
    @Provides
    fun provideReactionsActor(reactionsRepository: ReactionsRepository): ReactionsActor {
        return ReactionsActor(reactionsRepository)
    }
}