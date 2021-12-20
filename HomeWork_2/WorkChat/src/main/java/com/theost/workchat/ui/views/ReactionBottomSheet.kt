package com.theost.workchat.ui.views

import android.app.Activity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.theost.workchat.data.models.ui.ListReaction
import com.theost.workchat.databinding.ReactionsBottomSheetBinding
import com.theost.workchat.ui.adapters.core.BaseAdapter
import com.theost.workchat.ui.interfaces.DelegateItem
import com.theost.workchat.ui.adapters.delegates.ReactionAdapterDelegate

class ReactionBottomSheet(
    activity: Activity,
    private val callback: (reaction: ListReaction) -> Unit
) {

    private var binding: ReactionsBottomSheetBinding =
        ReactionsBottomSheetBinding.inflate(activity.layoutInflater)
    private val dialog = BottomSheetDialog(activity)
    private val adapter = BaseAdapter()

    init {
        dialog.setContentView(binding.root)
    }

    fun build(): BottomSheetDialog {
        val reactionList = listOf<DelegateItem>(
            ListReaction(100, "\uD83D\uDE0B"),
            ListReaction(101, "\uD83D\uDE0D"),
            ListReaction(102, "\uD83D\uDE00"),
            ListReaction(103, "\uD83D\uDE03"),
            ListReaction(104, "\uD83D\uDE09"),
            ListReaction(105, "\uD83D\uDE07"),
            ListReaction(106, "\uD83E\uDD29"),
            ListReaction(107, "\uD83D\uDE1B"),
            ListReaction(108, "\uD83E\uDD11"),
            ListReaction(109, "\uD83D\uDE36"),
            ListReaction(1010,"\uD83D\uDE44"),
            ListReaction(1011,"\uD83D\uDE26"),
            ListReaction(1012,"\uD83E\uDD7A"),
            ListReaction(1013,"\uD83D\uDE1E")
        )

        binding.reactionsList.adapter = adapter.apply {
            addDelegate(ReactionAdapterDelegate { reaction ->
                callback(reaction)
                dialog.dismiss()
            })
        }

        adapter.submitList(reactionList)

        return dialog
    }

}