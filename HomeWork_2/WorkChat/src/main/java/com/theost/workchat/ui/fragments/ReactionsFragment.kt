package com.theost.workchat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.ui.ListReaction
import com.theost.workchat.databinding.FragmentReactionsBinding
import com.theost.workchat.di.ui.DaggerReactionsComponent
import com.theost.workchat.elm.reactions.*
import com.theost.workchat.ui.adapters.core.BaseAdapter
import com.theost.workchat.ui.adapters.delegates.ReactionAdapterDelegate
import com.theost.workchat.ui.widgets.ElmBottomSheetFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class ReactionsFragment(
    private val clickListener: (reaction: ListReaction) -> Unit
) : ElmBottomSheetFragment<ReactionsEvent, ReactionsEffect, ReactionsState>() {

    @Inject
    lateinit var actor: ReactionsActor

    private val adapter: BaseAdapter = BaseAdapter()

    private var _binding: FragmentReactionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReactionsBinding.inflate(layoutInflater)

        binding.reactionsList.adapter = adapter.apply {
            addDelegate(ReactionAdapterDelegate { reaction ->
                store.accept(ReactionsEvent.Ui.ChooseReaction(reaction))
            })
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerReactionsComponent.factory().create(WorkChatApp.appComponent).inject(this)
    }

    override val initEvent: ReactionsEvent = ReactionsEvent.Ui.LoadReactions

    override fun createStore(): Store<ReactionsEvent, ReactionsEffect, ReactionsState> {
        return ReactionsStore.getStore(actor, ReactionsState())
    }

    override fun render(state: ReactionsState) {
        adapter.submitList(state.reactions)
    }

    override fun handleEffect(effect: ReactionsEffect) {
        when (effect) {
            is ReactionsEffect.ShowLoading -> showLoading()
            is ReactionsEffect.HideLoading -> hideLoading()
            is ReactionsEffect.ChooseReaction -> chooseReaction(effect.reaction)
        }
    }

    private fun chooseReaction(reaction: ListReaction) {
        clickListener(reaction)
        dismiss()
    }

    private fun hideLoading() {
        binding.loadingBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.loadingBar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newFragment(clickListener: (reaction: ListReaction) -> Unit): BottomSheetDialogFragment {
            val fragment = ReactionsFragment(clickListener)
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

}