package com.theost.workchat.di.ui


import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.di.base.AppComponent
import com.theost.workchat.di.base.ScreenScope
import com.theost.workchat.elm.messenger.MessengerActor
import com.theost.workchat.ui.activities.MessengerActivity
import dagger.Component
import dagger.Module
import dagger.Provides

@ScreenScope
@Component(dependencies = [AppComponent::class], modules = [MessengerModule::class])
interface MessengerComponent {
    fun inject(fragment: MessengerActivity)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): MessengerComponent
    }
}

@Module
class MessengerModule {
    @Provides
    fun provideMessengerActor(usersRepository: UsersRepository): MessengerActor {
        return MessengerActor(usersRepository)
    }
}