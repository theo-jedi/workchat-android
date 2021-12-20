package com.theost.workchat.elm.dialog

import com.theost.workchat.data.models.state.ResourceType
import com.theost.workchat.data.models.state.UpdateType
import com.theost.workchat.data.models.ui.ListMessage

sealed class DialogCommand {
    data class LoadMessages(
        val channelName: String,
        val topicName: String,
        val currentUserId: Int,
        val resourceType: ResourceType,
        val updateType: UpdateType
    ) : DialogCommand()

    data class LoadNextMessages(
        val channelName: String,
        val topicName: String,
        val currentUserId: Int,
        val messages: List<ListMessage>
    ) : DialogCommand()

    data class LoadItems(
        val messages: List<ListMessage>,
        val updateType: UpdateType
    ) : DialogCommand()

    data class LoadMessage(
        val channelName: String,
        val topicName: String,
        val currentUserId: Int,
        val messages: List<ListMessage>,
        val messageId: Int,
    ) : DialogCommand()

    data class AddMessage(
        val channelName: String,
        val topicName: String,
        val content: String
    ) : DialogCommand()

    data class AddReaction(val messageId: Int, val reactionName: String) : DialogCommand()

    data class RemoveReaction(
        val messageId: Int,
        val reactionName: String,
        val reactionCode: String,
        val reactionType: String
    ) : DialogCommand()
}