package com.theost.workchat.elm.creation.channel

import com.theost.workchat.data.models.state.CreationStatus
import com.theost.workchat.data.models.state.ResourceStatus
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class CreationChannelReducer :
    DslReducer<CreationChannelEvent, CreationChannelState, CreationChannelEffect, CreationChannelCommand>() {
    override fun Result.reduce(event: CreationChannelEvent): Any = when (event) {
        is CreationChannelEvent.Internal.DataLoadingSuccess -> {
            state { copy(status = ResourceStatus.SUCCESS, channels = event.channels) }
        }
        is CreationChannelEvent.Internal.DataSendingSuccess -> {
            state { copy(status = ResourceStatus.SUCCESS) }
            effects { +CreationChannelEffect.OpenChannels }
        }
        is CreationChannelEvent.Internal.DataLoadingError -> {
            state { copy(status = ResourceStatus.ERROR) }
            effects { +CreationChannelEffect.ShowLoadingError }
        }
        is CreationChannelEvent.Internal.DataSendingError -> {
            state { copy(status = ResourceStatus.ERROR) }
            effects { +CreationChannelEffect.HideSendingLoading }
            effects { +CreationChannelEffect.ShowSendingError }
        }
        is CreationChannelEvent.Ui.OnStateRestored -> {
            state { copy(stateStatus = ResourceStatus.SUCCESS) }
        }
        is CreationChannelEvent.Ui.LoadChannels -> {
            state { copy(status = ResourceStatus.LOADING) }
            commands { +CreationChannelCommand.LoadChannels }
        }
        is CreationChannelEvent.Ui.OnInputTextChanged -> {
            if (event.text.isNotEmpty()) {
                effects { +CreationChannelEffect.EnableSubmitButton }
                if (state.channels.contains(event.text)) {
                    if (state.creationStatus == CreationStatus.CHANNEL) {
                        state { copy(creationStatus = CreationStatus.TOPIC) }
                        effects { +CreationChannelEffect.SwitchTopicCreation }
                    } else {
                        { /* do nothing */ }
                    }
                } else {
                    if (state.creationStatus == CreationStatus.TOPIC) {
                        state { copy(creationStatus = CreationStatus.CHANNEL) }
                        effects { +CreationChannelEffect.SwitchChannelCreation }
                    } else {
                        { /* do nothing */ }
                    }
                }
            } else {
                effects { +CreationChannelEffect.DisableSubmitButton }
                if (state.creationStatus == CreationStatus.CHANNEL) {
                    state { copy(creationStatus = CreationStatus.TOPIC) }
                    effects { +CreationChannelEffect.SwitchTopicCreation }
                } else {
                    { /* do nothing */ }
                }
            }
        }
        is CreationChannelEvent.Ui.OnSubmitClicked -> {
            state {
                copy(
                    channelName = event.channelName,
                    channelDescription = event.channelDescription
                )
            }
            when (state.creationStatus) {
                CreationStatus.CHANNEL -> {
                    commands {
                        +CreationChannelCommand.CreateChannel(
                            event.channelName,
                            event.channelDescription
                        )
                    }
                    effects { +CreationChannelEffect.ShowSendingLoading }
                    effects { +CreationChannelEffect.HideKeyboard }
                }
                CreationStatus.TOPIC -> {
                    state { copy(stateStatus = ResourceStatus.LOADING) }
                    effects {
                        +CreationChannelEffect.OpenCreationTopic(
                            state.channelName,
                            state.channelDescription
                        )
                    }
                }
            }
        }
    }
}