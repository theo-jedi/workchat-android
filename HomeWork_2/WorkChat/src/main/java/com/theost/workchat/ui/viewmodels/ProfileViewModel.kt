package com.theost.workchat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.core.RxResource
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.data.repositories.UsersRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class ProfileViewModel : ViewModel() {

    private val _allData = MutableLiveData<ListUser>()
    val allData: LiveData<ListUser> = _allData

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    fun loadData(userId: Int) {
        _loadingStatus.postValue(ResourceStatus.LOADING)
        Single.zip(
            UsersRepository.getUser(userId),
            UsersRepository.getUserPresence(userId)
        ) {  userResource, presenceResource ->
            val error = userResource.error
            if (error == null) {
                RxResource.success(Pair(userResource.data, presenceResource.data))
            } else {
                RxResource.error(error, null)
            }
        }.subscribeOn(Schedulers.io()).subscribe({ resource ->
            if (resource.data?.first != null) {
                val user = resource.data.first!!
                val presence = resource.data.second
                val listUser = ListUser(
                    id = user.id,
                    name = user.name,
                    about = user.about,
                    avatarUrl = user.avatarUrl,
                    status = presence ?: UserStatus.OFFLINE
                )
                _allData.postValue(listUser)
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