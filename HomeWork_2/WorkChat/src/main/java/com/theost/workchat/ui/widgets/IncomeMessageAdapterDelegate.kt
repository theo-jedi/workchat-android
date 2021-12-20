package com.theost.workchat.ui.widgets

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.R
import com.theost.workchat.data.models.ListMessage
import com.theost.workchat.data.models.MessageAction
import com.theost.workchat.data.models.MessageType
import com.theost.workchat.ui.views.IncomeMessageView
import com.theost.workchat.ui.views.ReactionLayout
import com.theost.workchat.ui.views.ReactionView


class IncomeMessageAdapterDelegate(private val actionListener: (messageId: Int, reactionId: Int, actionType: MessageAction) -> Unit) :
    AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val messageView = IncomeMessageView(parent.context)
        return ViewHolder(messageView, actionListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListMessage)
    }

    override fun isOfViewType(item: Any): Boolean =
        item is ListMessage && item.messageType == MessageType.INCOME

    class ViewHolder(
        private val messageView: IncomeMessageView,
        private val actionListener: (messageId: Int, reactionId: Int, actionType: MessageAction) -> Unit
    ) :
        RecyclerView.ViewHolder(messageView) {

        fun bind(listMessage: ListMessage) {
            messageView.avatar = listMessage.avatar ?: R.mipmap.ic_launcher_round
            messageView.username = listMessage.username
            messageView.message = listMessage.message
            messageView.time = listMessage.time
            messageView.findViewById<View>(R.id.messageLayout).setOnLongClickListener {
                actionListener(listMessage.id, 0, MessageAction.REACTION_ADD)
                true
            }

            val emojiWidth =
                messageView.context.resources.getDimension(R.dimen.emoji_view_width).toInt()
            val emojiHeight =
                messageView.context.resources.getDimension(R.dimen.emoji_view_height).toInt()
            val reactionLayout = messageView.findViewById<ReactionLayout>(R.id.reactionLayout)
                .apply { removeAllViews() }

            if (listMessage.reactions.isNotEmpty()) {
                reactionLayout.addView(
                    ImageView(messageView.context).apply {
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
                    ReactionView(messageView.context).apply {
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