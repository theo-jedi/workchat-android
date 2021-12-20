package com.theost.workchat.elm.profile

import com.theost.workchat.data.models.ui.ListUser

sealed class ProfileEvent {
    sealed class Ui : ProfileEvent() {
        object LoadProfile : Ui()
    }

    sealed class Internal : ProfileEvent() {
        data class ProfileLoadingSuccess(val profile: ListUser) : Internal()
        data class DataLoadingError(val error: Throwable) : Internal()
    }
}