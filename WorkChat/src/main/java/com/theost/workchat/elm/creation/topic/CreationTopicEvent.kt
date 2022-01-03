package com.theost.workchat.elm.creation.topic

sealed class CreationTopicEvent {
    sealed class Ui : CreationTopicEvent() {
        object Init : Ui()
        data class OnInputTextChanged(val topicName: String, val topicMessage: String) : Ui()
        data class OnSubmitClicked(val topicName: String, val topicMessage: String) : Ui()
    }

    sealed class Internal : CreationTopicEvent() {
        object DataSendingSuccess : Internal()

        data class DataSendingError(val error: Throwable) : Internal()
    }
}