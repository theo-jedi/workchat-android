package com.theost.workchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.ui.ListReaction
import com.theost.workchat.databinding.FragmentReactionsSheetBinding
import com.theost.workchat.ui.adapters.core.BaseAdapter
import com.theost.workchat.ui.adapters.delegates.ReactionAdapterDelegate
import com.theost.workchat.ui.viewmodels.ReactionsViewModel

class ReactionBottomSheetFragment(
    private val callback: (reaction: ListReaction) -> Unit
) : BottomSheetDialogFragment() {

    private val viewModel: ReactionsViewModel by viewModels()
    private val adapter = BaseAdapter()

    private var _binding: FragmentReactionsSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReactionsSheetBinding.inflate(layoutInflater)

        binding.retryButton.setOnClickListener { loadData() }

        binding.reactionsList.adapter = adapter.apply {
            addDelegate(ReactionAdapterDelegate { reaction ->
                callback(reaction)
                dismiss()
            })
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                ResourceStatus.LOADING -> showLoading()
                ResourceStatus.SUCCESS -> hideLoading()
                ResourceStatus.ERROR -> showLoadingError()
                else -> {}
            }
        }
        viewModel.allData.observe(viewLifecycleOwner) { adapter.submitList(it) }
        
        loadData()

        return binding.root
    }

    private fun loadData() {
        viewModel.loadData()
    }

    private fun hideLoading() {
        binding.loadingBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.loadingBar.visibility = View.VISIBLE
        binding.errorView.visibility = View.GONE
        binding.retryButton.visibility = View.GONE
    }

    private fun showLoadingError() {
        binding.loadingBar.visibility = View.GONE
        binding.errorView.visibility = View.VISIBLE
        binding.retryButton.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newFragment(callback: (reaction: ListReaction) -> Unit): BottomSheetDialogFragment {
            val fragment = ReactionBottomSheetFragment(callback)
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

}