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
import com.theost.workchat.ui.interfaces.AdapterDelegate
import com.theost.workchat.ui.views.MessageIncomeView
import com.theost.workchat.ui.views.ReactionLayout
import com.theost.workchat.ui.views.ReactionView

class MessageIncomeAdapterDelegate(private val actionListener: (actionType: MessageAction, messageId: Int, reactionName: String) -> Unit) :
    AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val messageView = MessageIncomeView(parent.context)
        return ViewHolder(messageView, actionListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListMessage)
    }

    override fun isOfViewType(item: Any): Boolean =
        item is ListMessage && item.messageType == MessageType.INCOME

    class ViewHolder(
        private val messageIncomeView: MessageIncomeView,
        private val actionListener: (actionType: MessageAction, messageId: Int, reactionName: String) -> Unit
    ) :
        RecyclerView.ViewHolder(messageIncomeView) {

        fun bind(listMessage: ListMessage) {
            messageIncomeView.avatar = listMessage.senderAvatarUrl
            messageIncomeView.username = listMessage.senderName
            messageIncomeView.message = listMessage.content
            messageIncomeView.time = listMessage.time
            messageIncomeView.findViewById<View>(R.id.messageLayout).setOnLongClickListener {
                actionListener(MessageAction.REACTION_CHOOSE, listMessage.id, "")
                true
            }

            val emojiWidth =
                messageIncomeView.context.resources.getDimension(R.dimen.emoji_view_width).toInt()
            val emojiHeight =
                messageIncomeView.context.resources.getDimension(R.dimen.emoji_view_height).toInt()
            val reactionLayout = messageIncomeView.findViewById<ReactionLayout>(R.id.reactionLayout)
                .apply { removeAllViews() }

            if (listMessage.reactions.isNotEmpty()) {
                reactionLayout.addView(
                    ImageView(messageIncomeView.context).apply {
                        minimumWidth = emojiWidth
                        minimumHeight = emojiHeight
                        scaleType = ImageView.ScaleType.CENTER
                        setBackgroundResource(R.drawable.bg_reaction_view)
                        setImageResource(R.drawable.ic_add)
                        setOnClickListener {
                            actionListener(MessageAction.REACTION_CHOOSE, listMessage.id, "")
                        }
                    }
                )
            }

            listMessage.reactions.forEach { reaction ->
                reactionLayout.addView(
                    ReactionView(messageIncomeView.context).apply {
                        emoji = reaction.emoji
                        count = reaction.count
                        isSelected = reaction.isSelected
                        setOnClickListener { reactionView ->
                            reactionView.isSelected = !reactionView.isSelected
                            if (reactionView.isSelected) {
                                actionListener(
                                    MessageAction.REACTION_ADD,
                                    listMessage.id,
                                    reaction.name
                                )
                            } else {
                                actionListener(
                                    MessageAction.REACTION_REMOVE,
                                    listMessage.id,
                                    reaction.name
                                )
                            }
                        }
                    }, reactionLayout.size - 1
                )
            }
        }
    }
}