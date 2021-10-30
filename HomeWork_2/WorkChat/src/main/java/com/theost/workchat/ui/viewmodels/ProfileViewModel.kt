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
            _allData.postValue(resource.data)
            _loadingStatus.postValue(resource.status)
        }, {
            it.printStackTrace()
            _loadingStatus.postValue(ResourceStatus.ERROR)
        })
    }

}