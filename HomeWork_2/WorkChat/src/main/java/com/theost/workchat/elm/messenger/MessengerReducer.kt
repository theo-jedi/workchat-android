package com.theost.workchat.elm.messenger

import android.util.Log
import com.theost.workchat.R
import com.theost.workchat.data.models.state.ResourceStatus
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class MessengerReducer :
    DslReducer<MessengerEvent, MessengerState, MessengerEffect, MessengerCommand>() {
    override fun Result.reduce(event: MessengerEvent): Any = when (event) {
        is MessengerEvent.Internal.DataLoadingSuccess -> {
            state { copy(status = ResourceStatus.SUCCESS, currentUserId = event.userId) }
        }
        is MessengerEvent.Internal.DataLoadingError -> {
            /* do nothing */
        }
        is MessengerEvent.Ui.OnBackPress -> {
            effects { +MessengerEffect.HideFloatingViews }
        }
        is MessengerEvent.Ui.OnProfileClick -> {
            effects { +MessengerEffect.HideFloatingViews }
            effects { +MessengerEffect.HideNavigation }
            effects { +MessengerEffect.OpenProfile(event.userId) }
        }
        is MessengerEvent.Ui.OnDialogClick -> {
            effects { +MessengerEffect.HideFloatingViews }
            effects { +MessengerEffect.HideNavigation }
            effects { +MessengerEffect.OpenDialog(event.channelName, event.topicName) }
        }
        is MessengerEvent.Ui.OnNavigationClick -> {
            if (state.currentUserId == -1) {
                state { copy(status = ResourceStatus.LOADING) }
                commands { +MessengerCommand.LoadUser }
            }
            if (state.selectedItemId != event.itemId) {
                state { copy(selectedItemId = event.itemId) }
                effects { +MessengerEffect.HideFloatingViews }
                when (event.itemId) {
                    R.id.navChannels -> effects { +MessengerEffect.NavigateStreams }
                    R.id.navPeople -> effects { +MessengerEffect.NavigatePeople }
                    R.id.navProfile -> effects { +MessengerEffect.NavigateProfile }
                    else -> {}
                }
            } else {
                Log.d("messenger_reducer", "Fragment is already opened")
            }
        }
        is MessengerEvent.Ui.Init -> {
            if (state.currentUserId == -1) {
                state { copy(status = ResourceStatus.LOADING) }
                commands { +MessengerCommand.LoadUser }
            }
            state { copy(selectedItemId = event.itemId) }
            effects { +MessengerEffect.SelectNavigation(event.itemId) }
            when (event.itemId) {
                R.id.navChannels -> effects { +MessengerEffect.NavigateStreams }
                R.id.navPeople -> effects { +MessengerEffect.NavigatePeople }
                R.id.navProfile -> effects { +MessengerEffect.NavigateProfile }
                else -> {}
            }
        }
    }
}