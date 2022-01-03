package com.theost.workchat.di.ui

import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.di.base.AppComponent
import com.theost.workchat.di.base.ScreenScope
import com.theost.workchat.elm.profile.ProfileActor
import com.theost.workchat.ui.fragments.ProfileFragment
import dagger.Component
import dagger.Module
import dagger.Provides

@ScreenScope
@Component(dependencies = [AppComponent::class], modules = [ProfileModule::class])
interface ProfileComponent {
    fun inject(fragment: ProfileFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): ProfileComponent
    }
}

@Module
class ProfileModule {
    @Provides
    fun provideProfileActor(usersRepository: UsersRepository): ProfileActor {
        return ProfileActor(usersRepository)
    }
}