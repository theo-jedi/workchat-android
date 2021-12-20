package com.theost.workchat.ui.viewmodels



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.repositories.UsersRepository

class MessengerViewModel : ViewModel() {

    private val _currentUserId = MutableLiveData<Int>()
    val currentUserId: LiveData<Int> = _currentUserId

    fun updateData() {
        UsersRepository.getUser().subscribe({
            if (it.error == null && it.data != null) {
                _currentUserId.postValue(it.data.id)
            }
        }, {})
    }

}