package com.theost.workchat.data.models.alias

import com.theost.workchat.data.models.state.MessageAction
import com.theost.workchat.data.models.ui.ListMessageReaction

typealias ReactionListener = (actionType: MessageAction, messageId: Int, reaction: ListMessageReaction) -> Unit