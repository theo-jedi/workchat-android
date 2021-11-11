package com.theost.workchat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.ui.interfaces.DelegateItem

class PeopleViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<DelegateItem>>()
    val allData: LiveData<List<DelegateItem>> = _allData

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    private var usersList: List<ListUser> = listOf()

    fun loadData(currentUserId: Int) {
        if (allData.value == null) {
            _loadingStatus.postValue(ResourceStatus.LOADING)
            UsersRepository.getUsers().subscribe({ resource ->
                // Так как в бд может лежать профиль текущего юзера берём > 1
                if (resource.data != null && resource.data.size > 1) {
                    val users = resource.data
                    usersList = users
                        .filterNot { it.id == currentUserId }
                        .map { user ->
                            ListUser(
                                user.id,
                                user.name,
                                user.about,
                                user.avatarUrl,
                                UserStatus.OFFLINE
                            )
                        }.sortedBy { it.name }
                    _allData.postValue(usersList)
                    _loadingStatus.postValue(ResourceStatus.SUCCESS)
                } else {
                    resource.error?.printStackTrace()
                    //_loadingStatus.postValue(ResourceStatus.ERROR)
                }
            }, {
                it.printStackTrace()
                //_loadingStatus.postValue(ResourceStatus.ERROR)
            })
        }
    }

    fun filterData(filter: String) {
        if (allData.value != null) {
            val list = usersList.filter { user ->
                user.name.lowercase().contains(filter.trim().lowercase())
            }
            _allData.postValue(list)
        }
    }

}