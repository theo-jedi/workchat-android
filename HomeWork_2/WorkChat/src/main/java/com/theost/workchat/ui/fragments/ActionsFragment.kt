package com.theost.workchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theost.workchat.data.models.state.DialogAction
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.databinding.FragmentActionsBinding
import com.theost.workchat.elm.reactions.ReactionsActor
import javax.inject.Inject

class ActionsFragment(
    private val messageType: MessageType,
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

        if (messageType != MessageType.OUTCOME) {
            binding.editButton.visibility = View.GONE
            binding.deleteButton.visibility = View.GONE
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newFragment(
            messageType: MessageType,
            clickListener: (DialogAction) -> Unit
        ): BottomSheetDialogFragment {
            val fragment = ActionsFragment(messageType, clickListener)
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

}