package com.theost.workchat.elm.creation.topic

import com.theost.workchat.data.models.state.ResourceStatus
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class CreationTopicReducer :
    DslReducer<CreationTopicEvent, CreationTopicState, CreationTopicEffect, CreationTopicCommand>() {
    override fun Result.reduce(event: CreationTopicEvent): Any = when (event) {
        is CreationTopicEvent.Internal.DataSendingSuccess -> {
            state { copy(status = ResourceStatus.SUCCESS) }
            effects { +CreationTopicEffect.OpenChannels }
        }
        is CreationTopicEvent.Internal.DataSendingError -> {
            state { copy(status = ResourceStatus.ERROR) }
            effects { +CreationTopicEffect.HideLoading }
        }
        is CreationTopicEvent.Ui.OnInputTextChanged -> {
            if (event.topicName.isNotEmpty() && event.topicMessage.isNotEmpty()) {
                effects { +CreationTopicEffect.EnableSubmitButton }
            } else {
                effects { +CreationTopicEffect.DisableSubmitButton }
            }
        }
        is CreationTopicEvent.Ui.OnSubmitClicked -> {
            state {
                copy(
                    status = ResourceStatus.LOADING,
                    topicName = event.topicName,
                    topicMessage = event.topicMessage
                )
            }
            commands {
                +CreationTopicCommand.CreateChannelTopic(
                    state.channelName,
                    state.channelDescription,
                    state.topicName,
                    state.topicMessage
                )
            }
            effects { +CreationTopicEffect.ShowLoading }
            effects { +CreationTopicEffect.HideKeyboard }
        }
        is CreationTopicEvent.Ui.Init -> {
            /* do nothing */
        }
    }
}