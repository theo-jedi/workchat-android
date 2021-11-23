package com.theost.workchat.elm.profile

import com.theost.workchat.data.models.state.ResourceStatus
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class ProfileReducer : DslReducer<ProfileEvent, ProfileState, ProfileEffect, ProfileCommand>() {
    override fun Result.reduce(event: ProfileEvent): Any = when (event) {
        is ProfileEvent.Internal.ProfileLoadingSuccess -> {
            state { copy(status = ResourceStatus.SUCCESS, profile = event.profile) }
            effects { +ProfileEffect.HideLoading }
        }
        is ProfileEvent.Internal.DataLoadingError -> {
            state { copy(status = ResourceStatus.ERROR) }
            effects { +ProfileEffect.ShowError }
        }
        is ProfileEvent.Ui.LoadProfile -> {
            state { copy(status = ResourceStatus.LOADING) }
            commands { +ProfileCommand.LoadProfile(event.userId) }
            effects { +ProfileEffect.ShowLoading }
        }
        ProfileEvent.Ui.Init -> {
        }
    }
}