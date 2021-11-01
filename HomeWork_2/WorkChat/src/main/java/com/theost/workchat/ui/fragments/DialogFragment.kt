package com.theost.workchat.ui.fragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.data.models.state.InputStatus
import com.theost.workchat.data.models.state.MessageAction
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.state.ScrollStatus
import com.theost.workchat.databinding.FragmentDialogBinding
import com.theost.workchat.ui.adapters.core.BaseAdapter
import com.theost.workchat.ui.adapters.delegates.DateAdapterDelegate
import com.theost.workchat.ui.adapters.delegates.MessageIncomeAdapterDelegate
import com.theost.workchat.ui.adapters.delegates.MessageOutcomeAdapterDelegate
import com.theost.workchat.ui.viewmodels.DialogViewModel

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

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                ResourceStatus.SUCCESS -> { onDataLoaded() }
                ResourceStatus.ERROR -> { showLoadingError() }
                ResourceStatus.LOADING ->  {}
                else -> {}
            }
        }
        viewModel.sendingMessageStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                ResourceStatus.LOADING -> { onMessageSend() }
                ResourceStatus.SUCCESS -> { onMessageSent() }
                ResourceStatus.ERROR -> {
                    showMessageActionButton()
                    showSendingError()
                }
                else -> {}
            }
        }
        viewModel.sendingReactionStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                ResourceStatus.SUCCESS -> { loadData() }
                ResourceStatus.ERROR -> { showSendingError() }
                else -> {}
            }
        }
        viewModel.dialogInfo.observe(viewLifecycleOwner) { setDialogInfo(it.first, it.second) }
        viewModel.allData.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyLayout.emptyView.visibility = if (list.isNotEmpty()) View.GONE else View.VISIBLE
            if (scrollStatus == ScrollStatus.WAITING) {
                binding.messagesList.smoothScrollToPosition(adapter.itemCount + 1)
            }
        }
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
        onDataLoading()
        viewModel.loadData(dialogId, userId)
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
            InputStatus.EMPTY -> { /* todo file attachment */ }
            InputStatus.FILLED -> sendMessage()
        }
    }

    private fun sendMessage() {
        viewModel.sendMessage(getMessageText())
    }

    private fun onMessageAction(messageId: Int, reactionId: Int, actionType: MessageAction) {
        when (actionType) {
            MessageAction.REACTION_ADD -> pickReaction(messageId)
            MessageAction.REACTION_REMOVE -> viewModel.updateReaction(dialogId, messageId, reactionId)
        }
    }

    private fun pickReaction(messageId: Int) {
        if (!isDetached) {
            ReactionBottomSheetFragment.newFragment { reaction ->
                viewModel.updateReaction(dialogId, messageId, reaction.id, reaction.emoji)
            }.show(requireActivity().supportFragmentManager, null)
        }
    }

    private fun getMessageText(): String {
        return binding.inputLayout.messageInput.text.toString().trim()
    }

    private fun onMessageSend() {
        binding.inputLayout.actionButton.visibility = View.INVISIBLE
        binding.inputLayout.loadingBar.visibility = View.VISIBLE
    }

    private fun onMessageSent() {
        binding.inputLayout.messageInput.setText("")
        scrollStatus = ScrollStatus.WAITING
        showMessageActionButton()
        loadData()
    }

    private fun onDataLoading() {
        binding.loadingBar.visibility = View.VISIBLE
        binding.inputLayout.actionButton.animate().alpha(0.2f)
        binding.inputLayout.messageInput.animate().alpha(0.4f)
        binding.inputLayout.actionButton.isEnabled = false
        binding.inputLayout.messageInput.isEnabled = false
    }

    private fun onDataLoaded() {
        binding.loadingBar.visibility = View.GONE
        binding.inputLayout.actionButton.animate().alpha(1.0f)
        binding.inputLayout.messageInput.animate().alpha(1.0f)
        binding.inputLayout.actionButton.isEnabled = true
        binding.inputLayout.messageInput.isEnabled = true
    }

    private fun showMessageActionButton() {
        binding.inputLayout.actionButton.visibility = View.VISIBLE
        binding.inputLayout.loadingBar.visibility = View.GONE
    }

    private fun showSendingError() {
        Snackbar.make(binding.root, getString(R.string.network_error), Snackbar.LENGTH_INDEFINITE)
            .apply {
                anchorView = binding.inputLayout.messageInput
                setAction(R.string.hide) {}
            }.show()
    }

    private fun showLoadingError() {
        Snackbar.make(binding.root, getString(R.string.network_error), Snackbar.LENGTH_INDEFINITE)
            .apply {
                anchorView = binding.inputLayout.messageInput
                setAction(R.string.retry) { loadData() }
            }.show()
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