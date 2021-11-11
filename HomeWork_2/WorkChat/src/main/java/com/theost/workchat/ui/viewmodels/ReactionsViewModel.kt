package com.theost.workchat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListReaction
import com.theost.workchat.data.repositories.ReactionsRepository

class ReactionsViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<ListReaction>>()
    val allData: LiveData<List<ListReaction>> = _allData

    private val _loadingStatus = MutableLiveData<ResourceStatus>()
    val loadingStatus: LiveData<ResourceStatus> = _loadingStatus

    fun loadData() {
        _loadingStatus.postValue(ResourceStatus.LOADING)
        ReactionsRepository.getReactions().subscribe({ resource ->
            if (resource.data != null) {
                val reactions = resource.data.map {
                    ListReaction(
                        name = it.name,
                        code = it.code,
                        type = it.type,
                        emoji = it.emoji
                    )
                }
                _allData.postValue(reactions)
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