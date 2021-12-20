package com.theost.workchat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.data.repositories.UsersRepository

class ProfileViewModel : ViewModel() {

    private val _allData = MutableLiveData<ListUser>()
    val allData: LiveData<ListUser> = _allData

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    fun loadData(userId: Int) {
        _loadingStatus.postValue(ResourceStatus.LOADING)
        UsersRepository.getUser(userId).subscribe({ resource ->
            if (resource.data != null) {
                val user = resource.data
                val listUser = ListUser(
                    id = user.id,
                    name = user.name,
                    about = user.about,
                    avatarUrl = user.avatarUrl,
                    status = UserStatus.OFFLINE
                )
                _allData.postValue(listUser)
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