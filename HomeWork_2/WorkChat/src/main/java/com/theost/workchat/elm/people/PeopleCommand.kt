package com.theost.workchat.elm.people

import com.theost.workchat.data.models.ui.ListUser

sealed class PeopleCommand {
    data class LoadPeople(val currentUserId: Int) : PeopleCommand()
    data class LoadStatuses(val people: List<ListUser>) : PeopleCommand()

    data class SearchPeople(
        val query: String,
        val people: List<ListUser>
    ) : PeopleCommand()

    data class RestorePeople(
        val people: List<ListUser>
    ) : PeopleCommand()
}
