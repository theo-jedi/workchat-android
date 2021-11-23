package com.theost.workchat.elm.people

sealed class PeopleEffect {
    object ShowError : PeopleEffect()
    object ShowLoading : PeopleEffect()
    object HideLoading : PeopleEffect()
    object ShowEmpty : PeopleEffect()
    object HideEmpty : PeopleEffect()
    data class OpenProfile(val userId: Int) : PeopleEffect()
}
