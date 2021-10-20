package com.theost.workchat.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import com.theost.workchat.R
import com.theost.workchat.data.models.*
import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.data.repositories.ReactionsRepository
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.databinding.ActivityDialogBinding
import com.theost.workchat.ui.views.ReactionBottomSheet
import com.theost.workchat.ui.widgets.*
import com.theost.workchat.utils.DateUtils

class DialogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDialogBinding
    private val viewModel: DialogViewModel by viewModels()
    private val adapter = BaseAdapter()
    private var inputStatus = InputStatus.EMPTY
    private var dialogId: Int = 0
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialogId = intent.getIntExtra(DIALOG_ID_EXTRA, 0)

        binding.inputLayout.messageInput.addTextChangedListener { onInputTextChanged(it.toString()) }
        binding.inputLayout.actionButton.setOnClickListener { onInputActionClicked() }

        binding.messagesList.adapter = adapter
        adapter.apply {
            addDelegate(IncomeMessageAdapterDelegate() { messageId, reactionId, actionType ->
                onMessageAction(messageId, reactionId, actionType)
            })
            addDelegate(OutcomeMessageAdapterDelegate() { messageId, reactionId, actionType ->
                onMessageAction(messageId, reactionId, actionType)
            })
            addDelegate(DateAdapterDelegate())
        }

        viewModel.allData.observe(this) { setData(it.first, it.second) }

        loadData()
    }

    private fun loadData() {
        viewModel.loadData(dialogId)
    }

    private fun setData(items: List<Any>, reactions: List<Reaction>) {
        val listItems = mutableListOf<DelegateItem>()
        items.forEach { item ->
            when (item) {
                is Message -> {
                    val messageReactions = reactions.filter { it.messageId == item.id }.map {
                        ListMessageReaction(
                            it.id,
                            it.emoji,
                            it.userIds.size,
                            it.userIds.contains(userId)
                        )
                    }
                    listItems.add(
                        ListMessage(
                            item.id,
                            R.mipmap.ic_launcher_round,
                            UsersRepository.getUser(item.userId)?.name
                                ?: getString(R.string.deleted_user),
                            item.text,
                            DateUtils.getTime(item.date),
                            messageReactions,
                            if (item.userId == userId) MessageType.OUTCOME else MessageType.INCOME
                        )
                    )
                }
                is MessageDate -> {
                    listItems.add(ListDate(item.date))
                }
            }
        }
        adapter.submitList(listItems)
    }

    private fun onInputTextChanged(text: String) {
        inputStatus = if (text.trim().isNotEmpty()) {
            if (inputStatus == InputStatus.EMPTY)
                binding.inputLayout.actionButton.setImageResource(R.drawable.ic_send)
            InputStatus.FILLED
        } else {
            binding.inputLayout.actionButton.setImageResource(R.drawable.ic_attachment)
            InputStatus.EMPTY
        }
    }

    private fun onInputActionClicked() {
        when (inputStatus) {
            InputStatus.EMPTY -> println("todo") // todo file attachment
            InputStatus.FILLED -> sendMessage()
        }
    }

    private fun sendMessage() {
        val message = getMessageText()
        val isSent = MessagesRepository.addMessage(userId, dialogId, message)
        if (isSent) {
            loadData()
            binding.inputLayout.messageInput.setText("")
            binding.messagesList.smoothScrollToPosition(adapter.itemCount)
        } else {
            // todo send error bubble
            Toast.makeText(this, "Error, try again!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onMessageAction(messageId: Int, reactionId: Int, actionType: MessageAction) {
        when (actionType) {
            MessageAction.REACTION_ADD -> pickReaction(messageId)
            MessageAction.REACTION_EDIT -> editReaction(messageId, reactionId)
        }
    }

    private fun pickReaction(messageId: Int) {
        ReactionBottomSheet(this) { reaction ->
            sendReaction(messageId, reaction)
        }.build().show()
    }

    private fun sendReaction(messageId: Int, reaction: ListReaction) {
        val isSent = ReactionsRepository.addReaction(reaction.id, userId, messageId, reaction.emoji)
        if (isSent) {
            loadData()
        }
    }

    private fun editReaction(reactionId: Int, messageId: Int) {
        val isSent = ReactionsRepository.editReaction(reactionId, userId, messageId)
        if (isSent) {
            loadData()
        }
    }

    private fun getMessageText(): String {
        return binding.inputLayout.messageInput.text.toString().trim()
    }

    companion object {
        private const val DIALOG_ID_EXTRA = "dialog_id"

        fun createIntent(context: Context, dialogId: Int): Intent {
            val intent = Intent(context, DialogActivity::class.java)
            intent.putExtra(DIALOG_ID_EXTRA, dialogId)
            return intent
        }
    }

}