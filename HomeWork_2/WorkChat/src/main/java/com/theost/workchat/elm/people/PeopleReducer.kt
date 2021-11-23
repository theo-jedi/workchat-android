package com.theost.workchat.elm.people

import android.util.Log
import com.theost.workchat.data.models.state.ResourceStatus
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class PeopleReducer : DslReducer<PeopleEvent, PeopleState, PeopleEffect, PeopleCommand>() {
    override fun Result.reduce(event: PeopleEvent): Any = when (event) {
        is PeopleEvent.Internal.PeopleLoadingSuccess -> {
            if (event.people.isNotEmpty()) {
                state { copy(status = ResourceStatus.SUCCESS, people = event.people) }
                effects { +PeopleEffect.HideLoading }
            } else {
                Log.d("people_reducer", "People list is empty")
            }
        }
        is PeopleEvent.Internal.PeopleSearchingSuccess -> {
            state { copy(status = ResourceStatus.SUCCESS, searchedPeople = event.people) }
            if (event.people.isEmpty()) {
                effects { +PeopleEffect.ShowEmpty }
            } else {
                effects { +PeopleEffect.HideEmpty }
            }
        }
        is PeopleEvent.Internal.DataLoadingError -> {
            state { copy(status = ResourceStatus.ERROR) }
            effects { +PeopleEffect.ShowError }
        }
        is PeopleEvent.Ui.LoadPeople -> {
            state { copy(status = ResourceStatus.LOADING) }
            commands { +PeopleCommand.LoadPeople(event.currentUserId) }
            effects { +PeopleEffect.ShowLoading }
        }
        is PeopleEvent.Ui.SearchPeople -> {
            if (state.status != ResourceStatus.LOADING) {
                if (event.query.isNotEmpty()) {
                    state {
                        copy(
                            status = ResourceStatus.SEARCHING,
                            searchedPeople = emptyList(),
                            isSearchEnabled = true
                        )
                    }
                    commands { +PeopleCommand.SearchPeople(event.query, state.people) }
                } else {
                    state {
                        copy(
                            status = ResourceStatus.LOADING,
                            searchedPeople = emptyList(),
                            isSearchEnabled = false
                        )
                    }
                    commands { +PeopleCommand.RestorePeople(state.people) }
                    effects { +PeopleEffect.HideEmpty }
                }
            } else {
                Log.d("people_reducer", "Search is unavailable while loading data")
            }
        }
        is PeopleEvent.Ui.OpenProfile -> {
            effects { +PeopleEffect.OpenProfile(event.userId) }
        }
        PeopleEvent.Ui.Init -> {
        }
    }
}