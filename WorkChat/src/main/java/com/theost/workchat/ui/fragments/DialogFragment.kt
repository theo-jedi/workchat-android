package com.theost.workchat.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.state.ContentType
import com.theost.workchat.data.models.state.MessageAction
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.databinding.FragmentDialogBinding
import com.theost.workchat.di.ui.DaggerDialogComponent
import com.theost.workchat.elm.dialog.*
import com.theost.workchat.ui.adapters.callbacks.PaginationAdapterHelper
import com.theost.workchat.ui.adapters.core.PaginationAdapter
import com.theost.workchat.ui.adapters.delegates.*
import com.theost.workchat.ui.interfaces.WindowHolder
import com.theost.workchat.utils.ApiUtils
import com.theost.workchat.utils.ContextUtils
import com.theost.workchat.utils.FileUtils
import com.theost.workchat.utils.PrefUtils
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class DialogFragment : ElmFragment<DialogEvent, DialogEffect, DialogState>() {

    @Inject
    lateinit var actor: DialogActor

    private val adapter = PaginationAdapter(PaginationAdapterHelper { position ->
        store.accept(DialogEvent.Ui.LoadNextMessages(position))
    })

    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!

    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            store.accept(DialogEvent.Ui.OnItemsInserted)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = binding.messagesList.layoutManager as LinearLayoutManager
            val position = layoutManager.findFirstVisibleItemPosition()
            store.accept(DialogEvent.Ui.OnScrolled(position, dy))
        }
    }

    private val filePickerLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            context?.let { context ->
                result.data?.data?.let { uri ->
                    val file = FileUtils.createTempFileCopy(context, uri)
                    if (file != null) {
                        if (ApiUtils.isPhotoSizeValid(file.length())) {
                            store.accept(DialogEvent.Ui.OnPhotoSend(file))
                        } else {
                            store.accept(DialogEvent.Ui.OnPhotoCopyingSizeError)
                        }
                    } else {
                        store.accept(DialogEvent.Ui.OnPhotoCopyingFileError)
                    }
                }
            }
        } else {
            store.accept(DialogEvent.Ui.OnPhotoCopyingFileError)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentDialogBinding.inflate(layoutInflater)

        binding.emptyLayout.emptyView.text = getString(R.string.no_messages)

        binding.messagesList.adapter = adapter.apply {
            registerAdapterDataObserver(adapterDataObserver)
            addDelegate(LoaderAdapterDelegate())
            addDelegate(DateAdapterDelegate())
            addDelegate(PhotoIncomeAdapterDelegate())
            addDelegate(PhotoOutcomeAdapterDelegate())
            addDelegate(MessageIncomeAdapterDelegate({ dialogAction, message ->
                store.accept(
                    DialogEvent.Ui.OnMessageClicked(
                        dialogAction,
                        message.messageType,
                        message.contentType,
                        message.id,
                        message.content.toString()
                    )
                )
            }, { actionType, messageId, reaction ->
                store.accept(
                    DialogEvent.Ui.OnReactionClicked(
                        actionType, messageId, reaction.name, reaction.code, reaction.type
                    )
                )
            }))
            addDelegate(MessageOutcomeAdapterDelegate({ dialogAction, message ->
                store.accept(
                    DialogEvent.Ui.OnMessageClicked(
                        dialogAction,
                        message.messageType,
                        message.contentType,
                        message.id,
                        message.content.toString()
                    )
                )
            }, { actionType, messageId, reaction ->
                store.accept(
                    DialogEvent.Ui.OnReactionClicked(
                        actionType, messageId, reaction.name, reaction.code, reaction.type
                    )
                )
            }))
        }

        binding.messagesList.addOnScrollListener(scrollListener)

        binding.downButton.setOnClickListener { store.accept(DialogEvent.Ui.OnDownClicked) }

        binding.inputLayout.actionButton.setOnClickListener {
            store.accept(DialogEvent.Ui.OnMessageSendClicked(getMessageText()))
        }

        binding.inputLayout.messageInput.addTextChangedListener { editable ->
            store.accept(DialogEvent.Ui.OnInputTextChanged(editable.toString().trim()))
        }

        binding.inputLayout.editClose.setOnClickListener {
            store.accept(DialogEvent.Ui.OnCloseEdit)
        }

        binding.inputLayout.editButton.setOnClickListener {
            store.accept(DialogEvent.Ui.OnMessageEditClicked(binding.inputLayout.messageInput.text.toString()))
        }

        binding.inputLayout.photoClose.setOnClickListener {
            store.accept(DialogEvent.Ui.OnClosePhoto)
        }

        configureToolbar()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerDialogComponent.factory().create(WorkChatApp.appComponent).inject(this)
    }

    override val initEvent: DialogEvent = DialogEvent.Ui.LoadMessages

    override fun createStore(): Store<DialogEvent, DialogEffect, DialogState> {
        return DialogStore.getStore(
            actor,
            DialogState(
                currentUserId = context?.let { PrefUtils.getCurrentUserId(it) } ?: -1,
                channelName = arguments?.getString(CHANNEL_NAME_EXTRA) ?: "",
                topicName = arguments?.getString(TOPIC_NAME_EXTRA) ?: ""
            )
        )
    }

    override fun render(state: DialogState) {
        adapter.submitList(state.items)
    }

    override fun handleEffect(effect: DialogEffect) {
        when (effect) {
            is DialogEffect.ShowLoadingError -> showRetryError()
            is DialogEffect.ShowPaginationError -> showError()
            is DialogEffect.ShowSendingError -> showError()
            is DialogEffect.ShowPhotoCopyFileError -> showErrorToast(R.string.file_format_error)
            is DialogEffect.ShowPhotoCopySizeError -> showErrorToast(R.string.file_size_error)
            is DialogEffect.ShowSendingMessageLoading -> showSendingLoading()
            is DialogEffect.HideSendingMessageLoading -> hideSendingLoading()
            is DialogEffect.ShowEditingMessageLoading -> showEditingLoading()
            is DialogEffect.HideEditingMessageLoading -> hideEditingLoading()
            is DialogEffect.ClearSendingMessageContent -> clearMessageContent()
            is DialogEffect.ShowDialogLoading -> showDialogLoading()
            is DialogEffect.HideDialogLoading -> hideDialogLoading()
            is DialogEffect.ShowEmpty -> showEmptyView()
            is DialogEffect.HideEmpty -> hideEmptyView()
            is DialogEffect.ShowTitle -> configureTitle(effect.channel, effect.topic)
            is DialogEffect.ShowSendMessageAction -> showSendMessageAction()
            is DialogEffect.ShowSendEmptyMessageAction -> showSendEmptyMessageAction()
            is DialogEffect.ShowAttachMessageAction -> showAttachMessageAction()
            is DialogEffect.ScrollSmoothToBottom -> scrollSmoothToBottom()
            is DialogEffect.ScrollToBottom -> scrollToBottom()
            is DialogEffect.ScrollToTop -> scrollToTop(effect.position)
            is DialogEffect.ShowCopySuccess -> showCopySuccess()
            is DialogEffect.ShowCopyError -> showCopyError()
            is DialogEffect.CopyMessage -> copyMessage(effect.content)
            is DialogEffect.ShowMessageEdit -> showMessageEdit(effect.content)
            is DialogEffect.HideMessageEdit -> hideMessageEdit()
            is DialogEffect.ShowPhotoSend -> showPhotoSend(effect.content)
            is DialogEffect.HidePhotoSend -> hidePhotoSend()
            is DialogEffect.ShowDownButton -> showDownButton()
            is DialogEffect.HideDownButton -> hideDownButton()
            is DialogEffect.ShowFilePicker -> showFilePicker()
            is DialogEffect.ShowReactionPicker -> showReactionPicker(effect.messageId)
            is DialogEffect.ShowActionsPicker -> showActionsPicker(
                effect.messageType,
                effect.contentType,
                effect.messageId,
                effect.content
            )
        }
    }

    private fun copyMessage(content: String) {
        context?.let { context ->
            val isCopied = ContextUtils.copyToClipboard(context, "Message", content)
            store.accept(DialogEvent.Ui.OnMessageCopy(isCopied))
        }
    }

    private fun showMessageEdit(content: String) {
        binding.inputLayout.actionButton.visibility = View.INVISIBLE
        binding.inputLayout.editMessage.visibility = View.VISIBLE
        binding.inputLayout.editIcon.visibility = View.VISIBLE
        binding.inputLayout.editClose.visibility = View.VISIBLE
        binding.inputLayout.editButton.visibility = View.VISIBLE

        binding.inputLayout.editMessage.text = content
        binding.inputLayout.messageInput.setText(content)
        binding.inputLayout.messageInput.setSelection(content.length)
    }

    private fun hideMessageEdit() {
        binding.inputLayout.editMessage.visibility = View.GONE
        binding.inputLayout.editIcon.visibility = View.GONE
        binding.inputLayout.editClose.visibility = View.GONE
        binding.inputLayout.editButton.visibility = View.INVISIBLE
        binding.inputLayout.actionButton.visibility = View.VISIBLE
    }


    private fun showPhotoSend(content: String) {
        binding.inputLayout.photoMessage.visibility = View.VISIBLE
        binding.inputLayout.photoIcon.visibility = View.VISIBLE
        binding.inputLayout.photoClose.visibility = View.VISIBLE

        binding.inputLayout.photoMessage.text = content
        binding.inputLayout.messageInput.setText(content)
        binding.inputLayout.messageInput.setSelection(content.length)
    }

    private fun hidePhotoSend() {
        binding.inputLayout.photoMessage.visibility = View.GONE
        binding.inputLayout.photoIcon.visibility = View.GONE
        binding.inputLayout.photoClose.visibility = View.GONE
    }

    private fun showSendMessageAction() {
        binding.inputLayout.actionButton.setImageResource(R.drawable.ic_send)
    }

    private fun showSendEmptyMessageAction() {
        binding.inputLayout.actionButton.setImageResource(R.drawable.ic_send_empty)
    }

    private fun showAttachMessageAction() {
        binding.inputLayout.actionButton.setImageResource(R.drawable.ic_attachment)
    }

    private fun getMessageText(): String {
        return binding.inputLayout.messageInput.text.toString().trim()
    }

    private fun showDownButton() {
        binding.downButton.visibility = View.VISIBLE
    }

    private fun hideDownButton() {
        binding.downButton.visibility = View.GONE
    }

    private fun scrollSmoothToBottom() {
        binding.messagesList.smoothScrollToPosition(0)
    }

    private fun scrollToBottom() {
        binding.messagesList.scrollToPosition(0)
    }

    private fun scrollToTop(position: Int) {
        binding.messagesList.scrollToPosition(position)
    }

    private fun showEmptyView() {
        binding.emptyLayout.emptyView.visibility = View.VISIBLE
    }

    private fun hideEmptyView() {
        binding.emptyLayout.emptyView.visibility = View.GONE
    }

    private fun clearMessageContent() {
        binding.inputLayout.messageInput.setText("")
    }

    private fun showSendingLoading() {
        binding.inputLayout.actionButton.visibility = View.INVISIBLE
        binding.inputLayout.loadingBar.visibility = View.VISIBLE
    }

    private fun hideSendingLoading() {
        binding.inputLayout.actionButton.visibility = View.VISIBLE
        binding.inputLayout.loadingBar.visibility = View.GONE
    }

    private fun showEditingLoading() {
        binding.inputLayout.editButton.visibility = View.INVISIBLE
        binding.inputLayout.loadingBar.visibility = View.VISIBLE
        binding.inputLayout.messageInput.isEnabled = false
    }

    private fun hideEditingLoading() {
        binding.inputLayout.editButton.visibility = View.VISIBLE
        binding.inputLayout.loadingBar.visibility = View.GONE
        binding.inputLayout.messageInput.isEnabled = true
    }

    private fun showDialogLoading() {
        binding.loadingBar.visibility = View.VISIBLE
        binding.messagesList.visibility = View.GONE
        binding.inputLayout.actionButton.animate().alpha(0.2f)
        binding.inputLayout.messageInput.animate().alpha(0.4f)
        binding.inputLayout.actionButton.isEnabled = false
        binding.inputLayout.messageInput.isEnabled = false
    }

    private fun hideDialogLoading() {
        binding.loadingBar.visibility = View.GONE
        binding.messagesList.visibility = View.VISIBLE
        binding.inputLayout.actionButton.animate().alpha(1.0f)
        binding.inputLayout.messageInput.animate().alpha(1.0f)
        binding.inputLayout.actionButton.isEnabled = true
        binding.inputLayout.messageInput.isEnabled = true
    }

    private fun showCopySuccess() {
        context?.let { context ->
            Toast.makeText(context, getString(R.string.copied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCopyError() {
        context?.let { context ->
            Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showErrorToast(resId: Int) {
        context?.let { context -> Toast.makeText(context, resId, Toast.LENGTH_SHORT) }
    }

    private fun showError() {
        activity?.let { activity ->
            val snackbar = Snackbar.make(
                binding.root,
                getString(R.string.network_error),
                Snackbar.LENGTH_INDEFINITE
            ).apply { setAction(R.string.hide) { dismiss() } }
            (activity as WindowHolder).showSnackbar(snackbar, binding.inputLayout.root)
        }
    }

    private fun showRetryError() {
        activity?.let { activity ->
            val snackbar = Snackbar.make(
                binding.root,
                getString(R.string.network_error),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.retry) { store.accept(DialogEvent.Ui.LoadMessages) }
            (activity as WindowHolder).showSnackbar(snackbar, binding.inputLayout.root)
        }
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

    private fun showActionsPicker(
        messageType: MessageType,
        contentType: ContentType,
        messageId: Int,
        content: String
    ) {
        activity?.let { activity ->
            ActionsFragment.newFragment(messageType, contentType) { dialogAction ->
                store.accept(DialogEvent.Ui.OnDialogActionClicked(dialogAction, messageId, content))
            }.show(activity.supportFragmentManager, null)
        }
    }

    private fun showReactionPicker(messageId: Int) {
        activity?.let { activity ->
            ReactionsFragment.newFragment { reaction ->
                store.accept(
                    DialogEvent.Ui.OnReactionClicked(
                        MessageAction.REACTION_ADD,
                        messageId,
                        reaction.name,
                        reaction.code,
                        reaction.type
                    )
                )
            }.show(activity.supportFragmentManager, null)
        }
    }

    private fun showFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        filePickerLauncher.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.unregisterAdapterDataObserver(adapterDataObserver)
        binding.messagesList.removeOnScrollListener(scrollListener)
        _binding = null
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