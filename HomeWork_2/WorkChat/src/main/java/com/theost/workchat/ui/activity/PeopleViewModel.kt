package com.theost.workchat.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.ListUser
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.ui.widgets.DelegateItem

class PeopleViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<DelegateItem>>()
    val allData: LiveData<List<DelegateItem>> = _allData

    private var usersList: List<ListUser> = listOf()

    fun loadData(userId: Int) {
        usersList = UsersRepository.getUsers()
            .filterNot { it.id == userId }
            .map { user ->
                ListUser(
                    user.id,
                    user.name,
                    user.about,
                    user.avatar,
                    user.status
                )
            }
        _allData.postValue(usersList)
    }

    fun filterData(filter: String) {
        val list = usersList.filter { user ->
            user.name.lowercase().contains(filter.trim().lowercase())
        }
        _allData.postValue(list)
    }

}