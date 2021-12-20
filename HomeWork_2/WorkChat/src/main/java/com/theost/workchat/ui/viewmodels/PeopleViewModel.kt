package com.theost.workchat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.ui.interfaces.DelegateItem
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class PeopleViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<DelegateItem>>()
    val allData: LiveData<List<DelegateItem>> = _allData

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    private var usersList: List<ListUser> = listOf()

    fun loadData() {
        if (allData.value == null) {
            Single.zip(
                UsersRepository.getUsers(),
                UsersRepository.getUser()
            ) { usersResource, userResource ->
                val error = usersResource.error ?: userResource.error
                if (error == null) {
                    RxResource.success(Pair(usersResource.data, userResource.data))
                } else {
                    RxResource.error(error, null)
                }
            }.subscribeOn(Schedulers.io()).subscribe({ resource ->
                if (resource.data?.first != null) {
                    val users = resource.data.first!!
                    val userId = resource.data.second!!.id
                    usersList = users
                        .filterNot { it.id == userId }
                        .map { user ->
                            ListUser(
                                user.id,
                                user.name,
                                user.about,
                                user.avatarUrl,
                                UserStatus.OFFLINE
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