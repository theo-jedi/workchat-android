package com.theost.workchat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.core.User
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.repositories.UsersRepository

class ProfileViewModel : ViewModel() {

    private val _allData = MutableLiveData<User?>()
    val allData: LiveData<User?> = _allData

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    fun loadData(profileId: Int) {
        _loadingStatus.postValue(ResourceStatus.LOADING)
        UsersRepository.getUser(profileId).subscribe({ resource ->
            if (resource.data != null) {
                _allData.postValue(resource.data)
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