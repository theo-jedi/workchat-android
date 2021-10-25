package com.theost.workchat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.core.User
import com.theost.workchat.data.repositories.UsersRepository

class ProfileViewModel : ViewModel() {

    private val _allData = MutableLiveData<User?>()
    val allData: LiveData<User?> = _allData

    fun loadData(profileId: Int) {
        val user = UsersRepository.getUser(profileId)
        _allData.postValue(user)
    }

}