package com.theost.workchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.databinding.FragmentChannelsBinding
import com.theost.workchat.ui.adapters.core.BaseAdapter
import com.theost.workchat.ui.adapters.delegates.ChannelAdapterDelegate
import com.theost.workchat.ui.adapters.delegates.TopicAdapterDelegate
import com.theost.workchat.ui.interfaces.SearchHandler
import com.theost.workchat.ui.interfaces.TopicListener
import com.theost.workchat.ui.viewmodels.ChannelsViewModel
import com.theost.workchat.utils.PrefUtils

class ChannelsFragment : Fragment(), SearchHandler {

    private var channelsType = ChannelsType.ALL
    private val adapter = BaseAdapter()

    private var selectedChannelName: String = ""

    private val viewModel: ChannelsViewModel by viewModels()

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentChannelsBinding.inflate(layoutInflater)

        binding.channelsList.setHasFixedSize(true)
        binding.channelsList.adapter = adapter.apply {
            addDelegate(ChannelAdapterDelegate() { channelId, channelName, isSelected ->
                selectedChannelName = channelName
                viewModel.updateTopics(channelId, isSelected)
            })
            addDelegate(TopicAdapterDelegate() { topicName ->
                (activity as TopicListener).showTopicDialog(selectedChannelName, topicName)
            })
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                ResourceStatus.SUCCESS -> hideLoading()
                ResourceStatus.ERROR -> { showLoadingError() }
                ResourceStatus.LOADING ->  { showShimmerLayout() }
                else -> {}
            }
        }
        viewModel.subscribedChannelsIds.observe(viewLifecycleOwner) { channels ->
            context?.let { context ->
                PrefUtils.putSubscribedChannels(context, channels)
            }
        }
        viewModel.allData.observe(viewLifecycleOwner) { adapter.submitList(it) }

        loadData()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        channelsType = savedInstanceState?.getSerializable(CHANNELS_TYPE_EXTRA) as? ChannelsType
            ?: (arguments?.getSerializable(CHANNELS_TYPE_EXTRA) ?: 0) as ChannelsType
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loadData() {
        context?.let { context ->
            viewModel.loadData(channelsType, PrefUtils.getSubscribedChannels(context))
        }
    }

    override fun onSearch(query: String) {
        viewModel.filterData(query)
    }

    private fun hideLoading() {
        hideShimmerLayout()
    }

    private fun hideShimmerLayout() {
        binding.shimmerLayout.shimmer.visibility = View.GONE
        binding.channelsList.visibility = View.VISIBLE
    }

    private fun showShimmerLayout() {
        binding.shimmerLayout.shimmer.visibility = View.VISIBLE
        binding.channelsList.visibility = View.GONE
    }

    private fun showLoadingError() {
        Snackbar.make(binding.root, getString(R.string.network_error), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) { loadData() }
            .show()
    }

    companion object {
        private const val CHANNELS_TYPE_EXTRA = "channels_type"

        fun newFragment(channelsType: ChannelsType): Fragment {
            val fragment = ChannelsFragment()
            val bundle = Bundle()
            bundle.putSerializable(CHANNELS_TYPE_EXTRA, channelsType)
            fragment.arguments = bundle
            return fragment
        }
    }

}