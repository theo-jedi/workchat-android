package com.theost.workchat.ui.adapters.delegates

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.R
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.data.models.state.MessageAction
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.ui.interfaces.AdapterDelegate
import com.theost.workchat.ui.views.MessageOutcomeView
import com.theost.workchat.ui.views.ReactionLayout
import com.theost.workchat.ui.views.ReactionView


class OutcomeMessageAdapterDelegate(private val actionListener: (messageId: Int, reactionId: Int, actionType: MessageAction) -> Unit) :
    AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val messageView = MessageOutcomeView(parent.context)
        return ViewHolder(messageView, actionListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListMessage)
    }

    override fun isOfViewType(item: Any): Boolean =
        item is ListMessage && item.messageType == MessageType.OUTCOME

    class ViewHolder(
        private val messageOutcomeView: MessageOutcomeView,
        private val actionListener: (messageId: Int, reactionId: Int, actionType: MessageAction) -> Unit
    ) :
        RecyclerView.ViewHolder(messageOutcomeView) {

        fun bind(listMessage: ListMessage) {
            messageOutcomeView.message = listMessage.message
            messageOutcomeView.time = listMessage.time
            messageOutcomeView.findViewById<View>(R.id.messageLayout).setOnLongClickListener {
                actionListener(listMessage.id, 0, MessageAction.REACTION_ADD)
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
                        scaleType = ImageView.ScaleType.CENTER
                        setBackgroundResource(R.drawable.bg_reaction_view)
                        setImageResource(R.drawable.ic_add)
                        setOnClickListener {
                            actionListener(listMessage.id, 0, MessageAction.REACTION_ADD)
                        }
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
                            reactionView.isSelected = !reactionView.isSelected
                            actionListener(reaction.id, listMessage.id, MessageAction.REACTION_EDIT)
                        }
                    }, reactionLayout.size - 1
                )
            }
        }
    }
}