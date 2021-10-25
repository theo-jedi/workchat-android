package com.theost.workchat.ui.fragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.theost.workchat.R
import com.theost.workchat.data.models.core.Message
import com.theost.workchat.data.models.core.MessageDate
import com.theost.workchat.data.models.core.Reaction
import com.theost.workchat.data.models.state.InputStatus
import com.theost.workchat.data.models.state.MessageAction
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.data.models.state.ScrollStatus
import com.theost.workchat.data.models.ui.ListDate
import com.theost.workchat.data.models.ui.ListMessage
import com.theost.workchat.data.models.ui.ListMessageReaction
import com.theost.workchat.data.models.ui.ListReaction
import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.data.repositories.ReactionsRepository
import com.theost.workchat.data.repositories.UsersRepository
import com.theost.workchat.databinding.FragmentDialogBinding
import com.theost.workchat.ui.adapters.core.BaseAdapter
import com.theost.workchat.ui.adapters.delegates.DateAdapterDelegate
import com.theost.workchat.ui.adapters.delegates.MessageIncomeAdapterDelegate
import com.theost.workchat.ui.adapters.delegates.MessageOutcomeAdapterDelegate
import com.theost.workchat.ui.interfaces.DelegateItem
import com.theost.workchat.ui.viewmodels.DialogViewModel
import com.theost.workchat.ui.views.ReactionBottomSheet
import com.theost.workchat.utils.DateUtils

class DialogFragment : Fragment() {

    private val adapter = BaseAdapter()
    private var inputStatus = InputStatus.EMPTY
    private var scrollStatus = ScrollStatus.STAY
    private var dialogId: Int = 0
    private var userId: Int = 0

    private val viewModel: DialogViewModel by viewModels()

    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentDialogBinding.inflate(layoutInflater)

        configureToolbar()

        binding.emptyLayout.emptyView.text = getString(R.string.no_messages)
        binding.inputLayout.messageInput.addTextChangedListener { onInputTextChanged(it.toString()) }
        binding.inputLayout.actionButton.setOnClickListener { onInputActionClicked() }

        binding.messagesList.adapter = adapter.apply {
            addDelegate(MessageIncomeAdapterDelegate() { messageId, reactionId, actionType ->
                onMessageAction(messageId, reactionId, actionType)
            })
            addDelegate(MessageOutcomeAdapterDelegate() { messageId, reactionId, actionType ->
                onMessageAction(messageId, reactionId, actionType)
            })
            addDelegate(DateAdapterDelegate())
        }

        viewModel.dialogInfo.observe(viewLifecycleOwner) { setDialogInfo(it.first, it.second) }
        viewModel.allData.observe(viewLifecycleOwner) { setData(it.first, it.second) }
        loadData()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialogId = savedInstanceState?.getInt(DIALOG_ID_EXTRA)
            ?: (arguments?.getInt(DIALOG_ID_EXTRA) ?: 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun configureToolbar() {
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setDialogInfo(channel: String, topic: String) {
        val title = SpannableString("$channel $topic")
        title.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green)),
            channel.length,
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.toolbarLayout.toolbar.title = title
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
                            R.mipmap.sample_avatar,
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

        if (scrollStatus == ScrollStatus.WAITING) {
            scrollStatus = ScrollStatus.STAY
            binding.messagesList.smoothScrollToPosition(adapter.itemCount + 1)
        }

        if (listItems.isEmpty()) {
            binding.emptyLayout.emptyView.visibility = View.VISIBLE
        } else {
            binding.emptyLayout.emptyView.visibility = View.GONE
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
            scrollStatus = ScrollStatus.WAITING
            binding.inputLayout.messageInput.setText("")
            loadData()
        } else {
            // todo send error bubble
            Toast.makeText(context, "Error, try again!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onMessageAction(messageId: Int, reactionId: Int, actionType: MessageAction) {
        when (actionType) {
            MessageAction.REACTION_ADD -> pickReaction(messageId)
            MessageAction.REACTION_EDIT -> editReaction(messageId, reactionId)
        }
    }

    private fun pickReaction(messageId: Int) {
        if (!isDetached) {
            ReactionBottomSheet(requireActivity()) { reaction ->
                sendReaction(messageId, reaction)
            }.build().show()
        }
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

        fun newFragment(dialogId: Int): Fragment {
            val fragment = DialogFragment()
            val bundle = Bundle()
            bundle.putInt(DIALOG_ID_EXTRA, dialogId)
            fragment.arguments = bundle
            return fragment
        }
    }

}