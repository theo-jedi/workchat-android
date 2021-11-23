package com.theost.workchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.data.models.ui.ListChannel
import com.theost.workchat.databinding.FragmentChannelsBinding
import com.theost.workchat.elm.channels.ChannelsEffect
import com.theost.workchat.elm.channels.ChannelsEvent
import com.theost.workchat.elm.channels.ChannelsState
import com.theost.workchat.elm.channels.ChannelsStore
import com.theost.workchat.ui.adapters.core.BaseAdapter
import com.theost.workchat.ui.adapters.delegates.ChannelAdapterDelegate
import com.theost.workchat.ui.adapters.delegates.TopicAdapterDelegate
import com.theost.workchat.ui.interfaces.DelegateItem
import com.theost.workchat.ui.interfaces.SearchHandler
import com.theost.workchat.ui.interfaces.TopicListener
import com.theost.workchat.utils.PrefUtils
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store

class ChannelsFragment : ElmFragment<ChannelsEvent, ChannelsEffect, ChannelsState>(),
    SearchHandler {

    private var channelsType = ChannelsType.ALL
    private var subscribedChannels: List<Int> = emptyList()

    private val adapter = BaseAdapter()

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentChannelsBinding.inflate(layoutInflater)

        binding.emptyLayout.emptyView.text = getString(R.string.no_channels)

        binding.channelsList.setHasFixedSize(true)
        binding.channelsList.adapter = adapter.apply {
            addDelegate(ChannelAdapterDelegate { channelId, channelName, isSelected ->
                store.accept(ChannelsEvent.Ui.OnChannelClick(channelId, channelName, isSelected))
            })
            addDelegate(TopicAdapterDelegate { topicName ->
                store.accept(ChannelsEvent.Ui.OnTopicClick(topicName))
            })
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context?.let { context -> subscribedChannels = PrefUtils.getSubscribedChannels(context) }
        channelsType = savedInstanceState?.getSerializable(CHANNELS_TYPE_EXTRA) as? ChannelsType
            ?: arguments?.getSerializable(CHANNELS_TYPE_EXTRA) as? ChannelsType ?: ChannelsType.ALL

        store.accept(ChannelsEvent.Ui.LoadChannels(channelsType, subscribedChannels))
    }

    override val initEvent: ChannelsEvent = ChannelsEvent.Ui.Init // Wait for savedInstanceState

    override fun createStore(): Store<ChannelsEvent, ChannelsEffect, ChannelsState> =
        ChannelsStore().provide()

    override fun render(state: ChannelsState) {
        val channels = if (state.isSearchEnabled) state.searchedChannels else state.channels
        val items = channels.map {
            ListChannel(it.id, it.name, it.id == state.selectedChannelId)
        }.toMutableList<DelegateItem>()

        if (state.topics.isNotEmpty()) {
            val selectedIndex =
                items.indexOfFirst { it is ListChannel && it.id == state.selectedChannelId }
            if (selectedIndex != -1) items.addAll(selectedIndex + 1, state.topics)
        }

        if (channelsType == ChannelsType.SUBSCRIBED) {
            context?.let { context ->
                val subscribedChannels = state.channels.map { it.id }
                PrefUtils.putSubscribedChannels(context, subscribedChannels)
            }
        }

        adapter.submitList(items)
    }

    override fun handleEffect(effect: ChannelsEffect) {
        when (effect) {
            is ChannelsEffect.ShowError -> showLoadingError()
            is ChannelsEffect.ShowLoading -> showLoading()
            is ChannelsEffect.HideLoading -> hideLoading()
            is ChannelsEffect.ShowEmpty -> showEmptyView()
            is ChannelsEffect.HideEmpty -> hideEmptyView()
            is ChannelsEffect.OnChannelClick -> onChannelClick(effect.channelId, effect.channelName, effect.isSelected)
            is ChannelsEffect.OnTopicClick -> onTopicClick(effect.channelName, effect.topicName)
        }
    }

    private fun onChannelClick(channelId: Int, channelName: String, isSelected: Boolean) {
        if (!isSelected) {
            store.accept(ChannelsEvent.Ui.LoadTopics(channelId, channelName))
        } else {
            store.accept(ChannelsEvent.Ui.HideTopics)
        }
    }

    private fun onTopicClick(channelName: String, topicName: String) {
        (activity as TopicListener).showTopicDialog(channelName, topicName)
    }

    override fun onSearch(query: String) {
        store.accept(ChannelsEvent.Ui.SearchChannels(query))
    }

    private fun hideEmptyView() {
        binding.emptyLayout.emptyView.visibility = View.GONE
    }

    private fun showEmptyView() {
        binding.emptyLayout.emptyView.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.shimmerLayout.shimmer.visibility = View.GONE
        binding.channelsList.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.shimmerLayout.shimmer.visibility = View.VISIBLE
        binding.channelsList.visibility = View.GONE
    }

    private fun showLoadingError() {
        Snackbar.make(binding.root, getString(R.string.network_error), Snackbar.LENGTH_INDEFINITE)
            .apply {
                anchorView = binding.channelsList
                setAction(R.string.retry) {
                    store.accept(ChannelsEvent.Ui.LoadChannels(channelsType, subscribedChannels))
                }
            }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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