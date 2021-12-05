package com.theost.workchat.ui.adapters.delegates

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.R
import com.theost.workchat.data.models.alias.ReactionListener
import com.theost.workchat.data.models.state.MessageAction
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.ui.interfaces.AdapterDelegate
import com.theost.workchat.ui.interfaces.DelegateItem
import com.theost.workchat.ui.views.MessageIncomeView
import com.theost.workchat.ui.views.ReactionView
import com.theost.workchat.ui.views.ReactionsLayout

class MessageIncomeAdapterDelegate(
    private val messageListener: (messageId: Int) -> Unit,
    private val reactionListener: ReactionListener
) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(MessageIncomeView(parent.context), messageListener, reactionListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem, position: Int) {
        (holder as ViewHolder).bind(item as ListMessage)
    }

    override fun isOfViewType(item: DelegateItem): Boolean =
        item is ListMessage && item.messageType == MessageType.INCOME

    class ViewHolder(
        private val messageIncomeView: MessageIncomeView,
        private val messageListener: (messageId: Int) -> Unit,
        private val reactionListener: ReactionListener
    ) : RecyclerView.ViewHolder(messageIncomeView) {

        fun bind(listMessage: ListMessage) {
            messageIncomeView.avatar = listMessage.senderAvatarUrl
            messageIncomeView.username = listMessage.senderName
            messageIncomeView.message = listMessage.content
            messageIncomeView.time = listMessage.time
            messageIncomeView.findViewById<View>(R.id.messageLayout).setOnLongClickListener {
                messageListener(listMessage.id)
                true
            }

            val emojiWidth =
                messageIncomeView.context.resources.getDimension(R.dimen.emoji_view_width).toInt()
            val emojiHeight =
                messageIncomeView.context.resources.getDimension(R.dimen.emoji_view_height).toInt()
            val reactionsLayout = messageIncomeView.findViewById<ReactionsLayout>(R.id.reactionsLayout)
                .apply { removeAllViews() }

            if (listMessage.reactions.isNotEmpty()) {
                reactionsLayout.addView(
                    ImageView(messageIncomeView.context).apply {
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
                reactionsLayout.addView(
                    ReactionView(messageIncomeView.context).apply {
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
                    }, reactionsLayout.size - 1
                )
            }
        }
    }
}