package com.theost.workchat.di.ui

import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.data.repositories.ReactionsRepository
import com.theost.workchat.di.base.AppComponent
import com.theost.workchat.di.base.ScreenScope
import com.theost.workchat.elm.dialog.DialogActor
import com.theost.workchat.ui.fragments.DialogFragment
import dagger.Component
import dagger.Module
import dagger.Provides

@ScreenScope
@Component(dependencies = [AppComponent::class], modules = [DialogModule::class])
interface DialogComponent {
    fun inject(fragment: DialogFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): DialogComponent
    }
}

@Module
class DialogModule {
    @Provides
    fun provideDialogActor(
        messagesRepository: MessagesRepository,
        reactionsRepository: ReactionsRepository
    ): DialogActor {
        return DialogActor(messagesRepository, reactionsRepository)
    }
}