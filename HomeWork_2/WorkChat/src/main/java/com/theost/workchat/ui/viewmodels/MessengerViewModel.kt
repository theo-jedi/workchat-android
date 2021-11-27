package com.theost.workchat.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.repositories.UsersRepository
import io.reactivex.disposables.CompositeDisposable

class MessengerViewModel : ViewModel() {

    private val _currentUserId = MutableLiveData<Int>()
    val currentUserId: LiveData<Int> = _currentUserId

    private val compositeDisposable = CompositeDisposable()

    fun updateData() {
        compositeDisposable.add(
            UsersRepository.getUser().subscribe({ result ->
                result.fold({ user ->
                    _currentUserId.postValue(user.id)
                }, {
                    Log.d("messenger_view_model", "Unable to get current user id")
                })
            }, {
                Log.d("messenger_view_model", "Unable to get current user id")
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}