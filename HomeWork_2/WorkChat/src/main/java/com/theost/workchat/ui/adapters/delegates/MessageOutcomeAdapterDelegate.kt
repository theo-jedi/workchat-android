package com.theost.workchat.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.R
import com.theost.workchat.data.models.alias.ReactionListener
import com.theost.workchat.data.models.state.MessageAction
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.ui.interfaces.AdapterDelegate
import com.theost.workchat.ui.interfaces.DelegateItem
import com.theost.workchat.ui.views.MessageOutcomeView
import com.theost.workchat.ui.views.ReactionView

class MessageOutcomeAdapterDelegate(
    private val messageListener: (messageId: Int) -> Unit,
    private val reactionListener: ReactionListener
) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(MessageOutcomeView(parent.context), messageListener, reactionListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem, position: Int) {
        (holder as ViewHolder).bind(item as ListMessage)
    }

    override fun isOfViewType(item: DelegateItem): Boolean =
        item is ListMessage && item.messageType == MessageType.OUTCOME

    class ViewHolder(
        private val messageOutcomeView: MessageOutcomeView,
        private val messageListener: (messageId: Int) -> Unit,
        private val reactionListener: ReactionListener
    ) : RecyclerView.ViewHolder(messageOutcomeView) {

        fun bind(listMessage: ListMessage) {
            messageOutcomeView.message = listMessage.content
            messageOutcomeView.time = listMessage.time
            messageOutcomeView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            val reactionsLayout = messageOutcomeView.reactionsLayout.apply { removeAllViews() }
            messageOutcomeView.messageLayout.setOnLongClickListener {
                messageListener(listMessage.id)
                true
            }

            // Create add button if reactions not empty
            if (listMessage.reactions.isNotEmpty()) {
                val addReactionView = LayoutInflater.from(messageOutcomeView.context)
                    .inflate(R.layout.item_add_reaction, messageOutcomeView, false)
                    .apply { setOnClickListener { messageListener(listMessage.id) } }
                reactionsLayout.addView(addReactionView)
            }

            // Create reactions buttons
            listMessage.reactions.forEach { reaction ->
                val reactionView = (LayoutInflater.from(messageOutcomeView.context).inflate(
                    R.layout.item_message_reaction,
                    messageOutcomeView,
                    false
                ) as ReactionView).apply {
                    emoji = reaction.emoji
                    count = reaction.count
                    isSelected = reaction.isSelected
                    setOnClickListener { reactionView ->
                        if (!reactionView.isSelected) {
                            reactionListener(MessageAction.REACTION_ADD, listMessage.id, reaction)
                        } else {
                            reactionListener(MessageAction.REACTION_REMOVE, listMessage.id, reaction)
                        }
                    }
                }
                reactionsLayout.addView(reactionView, reactionsLayout.size - 1)
            }
        }
    }
}