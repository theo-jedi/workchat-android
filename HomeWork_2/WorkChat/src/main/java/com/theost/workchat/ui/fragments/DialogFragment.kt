package com.theost.workchat.ui.fragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.data.models.state.*
import com.theost.workchat.data.repositories.MessagesRepository
import com.theost.workchat.databinding.FragmentDialogBinding
import com.theost.workchat.ui.adapters.core.BaseAdapter
import com.theost.workchat.ui.adapters.delegates.DateAdapterDelegate
import com.theost.workchat.ui.adapters.delegates.MessageIncomeAdapterDelegate
import com.theost.workchat.ui.adapters.delegates.MessageOutcomeAdapterDelegate
import com.theost.workchat.ui.viewmodels.DialogViewModel
import com.theost.workchat.utils.PrefUtils

class DialogFragment : Fragment() {

    private val adapter = BaseAdapter()
    private var inputStatus = InputStatus.EMPTY
    private var scrollStatus = ScrollStatus.STAY

    private var channelName: String = ""
    private var topicName: String = ""

    private var isDialogLoaded: Boolean = false

    private var lastScrollPosition: Int = 0

    private val viewModel: DialogViewModel by viewModels()

    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!

    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            if (!isDialogLoaded) {
                onFirstDataLoaded()
            } else if (scrollStatus == ScrollStatus.WAITING) {
                scrollStatus = ScrollStatus.STAY
                binding.messagesList.smoothScrollToPosition(0)
            } else if (scrollStatus == ScrollStatus.IDLE) {
                scrollStatus = ScrollStatus.STAY
                // todo save position
            }
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val lastPosition =
                (binding.messagesList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            if (lastPosition > lastScrollPosition
                && adapter.itemCount - lastPosition <= MessagesRepository.DIALOG_NEXT_PAGE
            ) {
                loadNextData()
            }
            lastScrollPosition = lastPosition
        }
    }

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
            addDelegate(MessageIncomeAdapterDelegate() { actionType, messageId, reaction ->
                onMessageAction(
                    actionType,
                    messageId,
                    reaction?.name,
                    reaction?.code,
                    reaction?.type
                )
            })
            addDelegate(MessageOutcomeAdapterDelegate() { actionType, messageId, reaction ->
                onMessageAction(
                    actionType,
                    messageId,
                    reaction?.name,
                    reaction?.code,
                    reaction?.type
                )
            })
            addDelegate(DateAdapterDelegate())
            registerAdapterDataObserver(adapterDataObserver)
        }

        binding.messagesList.addOnScrollListener(scrollListener)

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                ResourceStatus.SUCCESS -> {
                    onDataLoaded()
                }
                ResourceStatus.ERROR -> {
                    showLoadingError()
                }
                ResourceStatus.LOADING -> {
                    onDataLoading()
                }
                else -> {
                }
            }
        }

        viewModel.paginationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                PaginationStatus.SUCCESS -> {
                    binding.paginationLoadingBar.visibility = View.GONE
                }
                PaginationStatus.ERROR -> {
                    binding.paginationLoadingBar.visibility = View.GONE
                    scrollStatus = ScrollStatus.STAY
                }
                PaginationStatus.LOADING -> {
                    binding.paginationLoadingBar.visibility = View.VISIBLE
                    scrollStatus = ScrollStatus.IDLE
                }
                PaginationStatus.FULLY_LOADED -> {
                    binding.paginationLoadingBar.visibility = View.GONE
                    scrollStatus = ScrollStatus.STAY
                }
                else -> {
                }
            }
        }

        viewModel.sendingMessageStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                ResourceStatus.LOADING -> {
                    showInputLoading()
                }
                ResourceStatus.SUCCESS -> {
                    onMessageSent()
                }
                ResourceStatus.ERROR -> {
                    hideInputLoading()
                    showSendingError()
                }
                else -> {
                }
            }
        }

        viewModel.sendingReactionStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                ResourceStatus.SUCCESS -> {
                    loadData()
                }
                ResourceStatus.ERROR -> {
                    showSendingError()
                }
                else -> {
                }
            }
        }

        viewModel.titleData.observe(viewLifecycleOwner) { configureTitle(it.first, it.second) }

        viewModel.allData.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.paginationLoadingBar.visibility = View.GONE
            binding.emptyLayout.emptyView.visibility =
                if (list.isNotEmpty()) View.GONE else View.VISIBLE
        }

        loadData()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        channelName = savedInstanceState?.getString(CHANNEL_NAME_EXTRA)
            ?: (arguments?.getString(CHANNEL_NAME_EXTRA) ?: "")
        topicName = savedInstanceState?.getString(TOPIC_NAME_EXTRA)
            ?: (arguments?.getString(TOPIC_NAME_EXTRA) ?: "")
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.unregisterAdapterDataObserver(adapterDataObserver)
        _binding = null
    }

    private fun configureToolbar() {
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun configureTitle(channel: String, topic: String) {
        val title = SpannableString("#$channel #$topic")
        context?.let { context ->
            title.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.green)),
                channel.length + 1,
                title.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.toolbarLayout.toolbar.title = title
    }

    private fun loadData() {
        context?.let { context ->
            viewModel.loadMessages(
                channelName,
                topicName,
                PrefUtils.getCurrentUserId(context)
            )
        }
    }

    private fun loadNextData() {
        context?.let { context ->
            viewModel.loadNextMessages(
                channelName,
                topicName,
                PrefUtils.getCurrentUserId(context)
            )
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
            InputStatus.EMPTY -> { /* todo file attachment */
            }
            InputStatus.FILLED -> sendMessage()
        }
    }

    private fun sendMessage() {
        viewModel.addMessage(getMessageText())
    }

    private fun onMessageAction(
        actionType: MessageAction,
        messageId: Int,
        reactionName: String?,
        reactionCode: String?,
        reactionType: String?
    ) {
        when (actionType) {
            MessageAction.REACTION_CHOOSE -> pickReaction(messageId)
            MessageAction.REACTION_ADD -> viewModel.addReaction(
                messageId = messageId,
                reactionName = reactionName.orEmpty()
            )
            MessageAction.REACTION_REMOVE -> viewModel.removeReaction(
                messageId = messageId,
                reactionName = reactionName.orEmpty(),
                reactionCode = reactionCode.orEmpty(),
                reactionType = reactionType.orEmpty()
            )
        }
    }

    private fun pickReaction(messageId: Int) {
        if (!isDetached) {
            ReactionBottomSheetFragment.newFragment { reaction ->
                onMessageAction(
                    MessageAction.REACTION_ADD,
                    messageId,
                    reaction.name,
                    reaction.code,
                    reaction.type
                )
            }.show(requireActivity().supportFragmentManager, null)
        }
    }

    private fun getMessageText(): String {
        return binding.inputLayout.messageInput.text.toString().trim()
    }

    private fun showInputLoading() {
        binding.inputLayout.actionButton.visibility = View.INVISIBLE
        binding.inputLayout.loadingBar.visibility = View.VISIBLE
    }

    private fun onMessageSent() {
        binding.inputLayout.messageInput.setText("")
        scrollStatus = ScrollStatus.WAITING
        loadData()
    }

    private fun onDataLoading() {
        if (!isDialogLoaded) {
            binding.loadingBar.visibility = View.VISIBLE
            binding.inputLayout.actionButton.animate().alpha(0.2f)
            binding.inputLayout.messageInput.animate().alpha(0.4f)
            binding.inputLayout.actionButton.isEnabled = false
            binding.inputLayout.messageInput.isEnabled = false
        }
    }

    private fun onFirstDataLoaded() {
        isDialogLoaded = true
        binding.loadingBar.visibility = View.GONE
        binding.inputLayout.actionButton.animate().alpha(1.0f)
        binding.inputLayout.messageInput.animate().alpha(1.0f)
        binding.inputLayout.actionButton.isEnabled = true
        binding.inputLayout.messageInput.isEnabled = true
        binding.messagesList.scrollToPosition(0)
    }

    private fun onDataLoaded() {
        hideInputLoading()
    }

    private fun hideInputLoading() {
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
        private const val CHANNEL_NAME_EXTRA = "channel_name"
        private const val TOPIC_NAME_EXTRA = "topic_name"

        fun newFragment(channelName: String, topicName: String): Fragment {
            val fragment = DialogFragment()
            val bundle = Bundle()
            bundle.putString(CHANNEL_NAME_EXTRA, channelName)
            bundle.putString(TOPIC_NAME_EXTRA, topicName)
            fragment.arguments = bundle
            return fragment
        }
    }

}