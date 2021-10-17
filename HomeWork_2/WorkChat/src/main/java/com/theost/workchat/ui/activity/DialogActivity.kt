package com.theost.workchat.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.size
import androidx.core.widget.addTextChangedListener
import com.theost.workchat.R
import com.theost.workchat.data.models.InputStatus
import com.theost.workchat.data.models.Message
import com.theost.workchat.data.models.MessageDate
import com.theost.workchat.data.models.Reaction
import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.data.repositories.ReactionsRepository
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.databinding.ActivityDialogBinding
import com.theost.workchat.ui.views.*
import com.theost.workchat.ui.widgets.ListReaction
import com.theost.workchat.utils.DateUtils

class DialogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDialogBinding
    private val viewModel: DialogViewModel by viewModels()
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

        viewModel.allData.observe(this) { setData(it) }

        loadData()
    }

    private fun loadData() {
        viewModel.loadData(dialogId)
    }

    private fun setData(items: List<Any>) {
        binding.messagesList.removeAllViews()
        items.forEach { item ->
            when (item) {
                is MessageDate -> loadDate(item)
                is Message -> loadMessage(item)
            }
        }
    }

    private fun loadDate(messageDate: MessageDate) {
        binding.messagesList.addView(
            DateView(this).apply { text = messageDate.date }
        )
    }

    private fun loadMessage(message: Message) {
        val reactions = ReactionsRepository.getReactions(message.id).sortedBy { it.date }
        if (message.userId == userId) {
            binding.messagesList.addView(
                OutcomeMessageView(this).apply {
                    this.message = message.text
                    this.time = DateUtils.getTime(message.date)
                    loadReactions(this, message, reactions)
                }
            )
        } else {
            binding.messagesList.addView(
                IncomeMessageView(this).apply {
                    this.avatar = R.mipmap.ic_launcher_round
                    this.username = UsersRepository.getUser(message.userId)?.name
                        ?: getString(R.string.deleted_user)
                    this.message = message.text
                    this.time = DateUtils.getTime(message.date)
                    loadReactions(this, message, reactions)
                    invalidate()
                }
            )
        }
    }

    private fun loadReactions(messageView: View, message: Message, reactions: List<Reaction>) {
        messageView.findViewById<ReactionLayout>(R.id.reactionLayout).apply {
            findViewById<ImageButton>(R.id.addReaction).setOnClickListener {
                pickReaction(message.id)
            }
            reactions.forEach { reaction ->
                addView(
                    ReactionView(context).apply {
                        emoji = reaction.emoji
                        count = reaction.userIds.size
                        isSelected = userId in reaction.userIds
                    }, size - 1
                )
            }
        }
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
            binding.dialogScroll.fullScroll(ScrollView.FOCUS_DOWN)
        } else {
            // todo send error bubble
            Toast.makeText(this, "Error, try again!", Toast.LENGTH_SHORT).show()
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