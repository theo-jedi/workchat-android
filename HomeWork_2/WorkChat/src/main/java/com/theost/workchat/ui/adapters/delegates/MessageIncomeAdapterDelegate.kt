package com.theost.workchat.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.theost.workchat.R
import com.theost.workchat.data.models.alias.ReactionListener
import com.theost.workchat.data.models.state.DialogAction
import com.theost.workchat.data.models.state.MessageAction
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.databinding.ItemMessageIncomeBinding
import com.theost.workchat.ui.interfaces.AdapterDelegate
import com.theost.workchat.ui.interfaces.DelegateItem
import com.theost.workchat.ui.views.MessageIncomeView
import com.theost.workchat.ui.views.ReactionView

class MessageIncomeAdapterDelegate(
    private val messageListener: (dialogAction: DialogAction, message: ListMessage) -> Unit,
    private val reactionListener: ReactionListener
) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            ItemMessageIncomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root, messageListener, reactionListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int
    ) {
        (holder as ViewHolder).bind(item as ListMessage)
    }

    override fun isOfViewType(item: DelegateItem): Boolean =
        item is ListMessage && item.messageType == MessageType.INCOME

    class ViewHolder(
        private val messageIncomeView: MessageIncomeView,
        private val messageListener: (dialogAction: DialogAction, message: ListMessage) -> Unit,
        private val reactionListener: ReactionListener
    ) : RecyclerView.ViewHolder(messageIncomeView) {

        fun bind(listMessage: ListMessage) {
            messageIncomeView.avatar = listMessage.senderAvatarUrl
            messageIncomeView.name = listMessage.senderName
            messageIncomeView.message = listMessage.content
            messageIncomeView.time = listMessage.time

            val reactionsLayout = messageIncomeView.reactionsLayout.apply { removeAllViews() }
            messageIncomeView.messageLayout.setOnLongClickListener {
                messageListener(DialogAction.SHOW_ACTIONS_PICKER, listMessage)
                true
            }

            // Create add button if reactions not empty
            if (listMessage.reactions.isNotEmpty()) {
                val addReactionView = LayoutInflater.from(messageIncomeView.context)
                    .inflate(R.layout.item_add_reaction, messageIncomeView, false)
                    .apply {
                        setOnClickListener {
                            messageListener(
                                DialogAction.SHOW_REACTION_PICKER,
                                listMessage
                            )
                        }
                    }
                reactionsLayout.addView(addReactionView)
            }

            // Create reactions buttons
            listMessage.reactions.forEach { reaction ->
                val reactionView = (LayoutInflater.from(messageIncomeView.context).inflate(
                    R.layout.item_message_reaction,
                    messageIncomeView,
                    false
                ) as ReactionView).apply {
                    emoji = reaction.emoji
                    count = reaction.count
                    isSelected = reaction.isSelected
                    setOnClickListener { reactionView ->
                        if (!reactionView.isSelected) {
                            reactionListener(MessageAction.REACTION_ADD, listMessage.id, reaction)
                        } else {
                            reactionListener(
                                MessageAction.REACTION_REMOVE,
                                listMessage.id,
                                reaction
                            )
                        }
                    }
                }
                reactionsLayout.addView(reactionView, reactionsLayout.size - 1)
            }
        }
    }
}