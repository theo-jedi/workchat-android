package com.theost.workchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theost.workchat.data.models.state.ContentType
import com.theost.workchat.data.models.state.DialogAction
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.databinding.FragmentActionsBinding
import com.theost.workchat.elm.reactions.ReactionsActor
import javax.inject.Inject

class ActionsFragment(
    private val clickListener: (dialogAction: DialogAction) -> Unit
) : BottomSheetDialogFragment() {

    @Inject
    lateinit var actor: ReactionsActor

    private var _binding: FragmentActionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActionsBinding.inflate(layoutInflater)

        val messageType = when (arguments?.getString(MESSAGE_TYPE_EXTRA)) {
            MessageType.OUTCOME.name -> MessageType.OUTCOME
            MessageType.INCOME.name -> MessageType.INCOME
            else -> MessageType.INCOME
        }

        val contentType = when (arguments?.getString(CONTENT_TYPE_EXTRA)) {
            ContentType.PHOTO.name -> ContentType.PHOTO
            ContentType.TEXT.name -> ContentType.TEXT
            else -> ContentType.TEXT
        }

        if (messageType == MessageType.INCOME) {
            binding.editButton.visibility = View.GONE
            binding.deleteButton.visibility = View.GONE
        } else if (contentType == ContentType.PHOTO) {
            binding.editButton.visibility = View.GONE
        }

        binding.reactionButton.setOnClickListener {
            clickListener(DialogAction.SHOW_REACTION_PICKER)
            dismiss()
        }

        binding.copyButton.setOnClickListener {
            clickListener(DialogAction.COPY_MESSAGE)
            dismiss()
        }
        binding.editButton.setOnClickListener {
            clickListener(DialogAction.EDIT_MESSAGE)
            dismiss()
        }

        binding.deleteButton.setOnClickListener {
            clickListener(DialogAction.DELETE_MESSAGE)
            dismiss()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val MESSAGE_TYPE_EXTRA = "message_type"
        private const val CONTENT_TYPE_EXTRA = "content_type"

        fun newFragment(
            messageType: MessageType,
            contentType: ContentType,
            clickListener: (DialogAction) -> Unit
        ): BottomSheetDialogFragment {
            val fragment = ActionsFragment(clickListener)
            val bundle = Bundle()
            bundle.putString(MESSAGE_TYPE_EXTRA, messageType.name)
            bundle.putString(CONTENT_TYPE_EXTRA, contentType.name)
            fragment.arguments = bundle
            return fragment
        }
    }

}