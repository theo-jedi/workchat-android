package com.theost.workchat.elm.people

import com.theost.workchat.data.models.ui.ListUser

sealed class PeopleEvent {
    sealed class Ui : PeopleEvent() {
        data class LoadPeople(val currentUserId: Int) : Ui()
        data class SearchPeople(val query: String) : Ui()
        data class OpenProfile(val userId: Int) : Ui()
        object Init : Ui()
    }

    sealed class Internal : PeopleEvent() {
        data class PeopleLoadingSuccess(val people: List<ListUser>) : Internal()
        data class PeopleSearchingSuccess(val people: List<ListUser>) : Internal()
        data class DataLoadingError(val error: Throwable) : Internal()
    }
}