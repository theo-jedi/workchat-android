package com.theost.workchat.di.ui

import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.di.base.AppComponent
import com.theost.workchat.di.base.ScreenScope
import com.theost.workchat.elm.people.PeopleActor
import com.theost.workchat.ui.fragments.PeopleFragment
import dagger.Component
import dagger.Module
import dagger.Provides

@ScreenScope
@Component(dependencies = [AppComponent::class], modules = [PeopleModule::class])
interface PeopleComponent {
    fun inject(fragment: PeopleFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): PeopleComponent
    }
}

@Module
class PeopleModule {
    @Provides
    fun providePeopleActor(usersRepository: UsersRepository): PeopleActor {
        return PeopleActor(usersRepository)
    }
}