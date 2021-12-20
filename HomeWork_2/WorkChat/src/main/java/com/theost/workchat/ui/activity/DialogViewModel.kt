package com.theost.workchat.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.workchat.data.models.MessageDate
import com.theost.workchat.data.models.Reaction
import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.data.repositories.ReactionsRepository
import com.theost.workchat.utils.DateUtils

class DialogViewModel : ViewModel() {

    private val _allData = MutableLiveData<Pair<List<Any>, List<Reaction>>>()
    val allData: LiveData<Pair<List<Any>, List<Reaction>>> = _allData

    fun loadData(dialogId: Int) {
        val items = mutableListOf<Any>()
        val messages = MessagesRepository.getMessages(dialogId).sortedBy { it.date }
        val reactions = mutableListOf<Reaction>()
        messages.forEach { reactions.addAll(ReactionsRepository.getReactions(it.id)) }
        for (i in messages.indices) {
            if (i == 0 || !DateUtils.isSameDay(messages[i].date, messages[i - 1].date)) {
                items.add(MessageDate(DateUtils.getDayDate(messages[i].date)))
            }
            items.add(messages[i])
        }
        _allData.postValue(Pair(items, reactions))
    }

}