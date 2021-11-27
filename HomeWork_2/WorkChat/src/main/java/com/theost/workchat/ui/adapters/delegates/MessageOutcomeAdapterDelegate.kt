package com.theost.workchat.ui.adapters.delegates

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.R
import com.theost.workchat.data.models.state.MessageAction
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.data.models.ui.ListMessageReaction
import com.theost.workchat.ui.interfaces.AdapterDelegate
import com.theost.workchat.ui.views.MessageOutcomeView
import com.theost.workchat.ui.views.ReactionLayout
import com.theost.workchat.ui.views.ReactionView


class MessageOutcomeAdapterDelegate(
    private val messageListener: (messageId: Int) -> Unit,
    private val reactionListener: (actionType: MessageAction, messageId: Int, reaction: ListMessageReaction) -> Unit
) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(MessageOutcomeView(parent.context), messageListener, reactionListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListMessage)
    }

    override fun isOfViewType(item: Any): Boolean =
        item is ListMessage && item.messageType == MessageType.OUTCOME

    class ViewHolder(
        private val messageOutcomeView: MessageOutcomeView,
        private val messageListener: (messageId: Int) -> Unit,
        private val reactionListener: (actionType: MessageAction, messageId: Int, reaction: ListMessageReaction) -> Unit
    ) : RecyclerView.ViewHolder(messageOutcomeView) {

        fun bind(listMessage: ListMessage) {
            messageOutcomeView.message = listMessage.content
            messageOutcomeView.time = listMessage.time
            messageOutcomeView.findViewById<View>(R.id.messageLayout).setOnLongClickListener {
                messageListener(listMessage.id)
                true
            }

            val emojiWidth =
                messageOutcomeView.context.resources.getDimension(R.dimen.emoji_view_width).toInt()
            val emojiHeight =
                messageOutcomeView.context.resources.getDimension(R.dimen.emoji_view_height).toInt()
            val reactionLayout = messageOutcomeView.findViewById<ReactionLayout>(R.id.reactionLayout)
                .apply { removeAllViews() }

            if (listMessage.reactions.isNotEmpty()) {
                reactionLayout.addView(
                    ImageView(messageOutcomeView.context).apply {
                        minimumWidth = emojiWidth
                        minimumHeight = emojiHeight
                        maxWidth = emojiWidth
                        maxHeight = emojiHeight
                        scaleType = ImageView.ScaleType.CENTER
                        setBackgroundResource(R.drawable.bg_reaction_view)
                        setImageResource(R.drawable.ic_add)
                        setOnClickListener { messageListener(listMessage.id) }
                    }
                )
            }

            listMessage.reactions.forEach { reaction ->
                reactionLayout.addView(
                    ReactionView(messageOutcomeView.context).apply {
                        emoji = reaction.emoji
                        count = reaction.count
                        isSelected = reaction.isSelected
                        setOnClickListener { reactionView ->
                            if (!reactionView.isSelected) {
                                reactionListener(
                                    MessageAction.REACTION_ADD,
                                    listMessage.id,
                                    reaction
                                )
                            } else {
                                reactionListener(
                                    MessageAction.REACTION_REMOVE,
                                    listMessage.id,
                                    reaction
                                )
                            }
                        }
                    }, reactionLayout.size - 1
                )
            }
        }
    }
}