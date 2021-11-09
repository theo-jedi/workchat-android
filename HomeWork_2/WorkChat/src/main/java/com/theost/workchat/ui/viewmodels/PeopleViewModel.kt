package com.theost.workchat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.ui.interfaces.DelegateItem

class PeopleViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<DelegateItem>>()
    val allData: LiveData<List<DelegateItem>> = _allData

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    private var usersList: List<ListUser> = listOf()

    fun loadData(userId: Int) {

        UsersRepository.getUsers().subscribe({ resource ->
            if (resource.data != null) {
                usersList = resource.data
                    .filterNot { it.id == userId }
                    .map { user ->
                        ListUser(
                            user.id,
                            user.name,
                            user.about,
                            user.avatarUrl,
                            user.isActive
                        )
                    }
                _allData.postValue(usersList)
                _loadingStatus.postValue(resource.status)
            } else {
                resource.error?.printStackTrace()
                _loadingStatus.postValue(ResourceStatus.ERROR)
            }
        }, {
            it.printStackTrace()
            _loadingStatus.postValue(ResourceStatus.ERROR)
        })
    }

    fun filterData(filter: String) {
        val list = usersList.filter { user ->
            user.name.lowercase().contains(filter.trim().lowercase())
        }
        _allData.postValue(list)
    }

}