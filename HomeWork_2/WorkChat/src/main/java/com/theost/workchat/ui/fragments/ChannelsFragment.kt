package com.theost.workchat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.databinding.FragmentChannelsBinding
import com.theost.workchat.di.ui.DaggerChannelsComponent
import com.theost.workchat.elm.channels.*
import com.theost.workchat.ui.adapters.core.BaseAdapter
import com.theost.workchat.ui.adapters.delegates.ChannelAdapterDelegate
import com.theost.workchat.ui.adapters.delegates.TopicAdapterDelegate
import com.theost.workchat.ui.interfaces.SearchHandler
import com.theost.workchat.ui.interfaces.TopicListener
import com.theost.workchat.ui.interfaces.WindowHolder
import com.theost.workchat.utils.PrefUtils
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class ChannelsFragment : ElmFragment<ChannelsEvent, ChannelsEffect, ChannelsState>(),
    SearchHandler {

    @Inject
    lateinit var actor: ChannelsActor

    private val adapter: BaseAdapter = BaseAdapter()

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerChannelsComponent.factory().create(WorkChatApp.appComponent).inject(this)
    }

    override val initEvent: ChannelsEvent = ChannelsEvent.Ui.LoadChannels

    override fun createStore(): Store<ChannelsEvent, ChannelsEffect, ChannelsState> {
        return ChannelsStore.getStore(
            actor,
            ChannelsState(
                channelsType = arguments?.getSerializable(CHANNELS_TYPE_EXTRA) as? ChannelsType
                    ?: ChannelsType.ALL,
                subscribedChannels = context?.let { PrefUtils.getSubscribedChannels(it) }
                    ?: emptyList()
            )
        )
    }

    override fun render(state: ChannelsState) {
        if (!state.isSearchEnabled && state.channelsType == ChannelsType.SUBSCRIBED) {
            context?.let { PrefUtils.putSubscribedChannels(it, state.subscribedChannels) }
        }

        adapter.submitList(state.items)
    }

    override fun handleEffect(effect: ChannelsEffect) {
        when (effect) {
            is ChannelsEffect.ShowError -> showLoadingError()
            is ChannelsEffect.ShowLoading -> showLoading()
            is ChannelsEffect.HideLoading -> hideLoading()
            is ChannelsEffect.ShowEmpty -> showEmptyView()
            is ChannelsEffect.HideEmpty -> hideEmptyView()
            is ChannelsEffect.OnTopicClicked -> onTopicClick(effect.channelName, effect.topicName)
            is ChannelsEffect.OnChannelClicked -> onChannelClick(
                effect.channelId,
                effect.channelName,
                effect.isSelected
            )
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
        (activity as TopicListener).openDialog(channelName, topicName)
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
        val snackbar = Snackbar.make(
            binding.root,
            getString(R.string.network_error),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.retry) { store.accept(ChannelsEvent.Ui.LoadChannels) }
        (activity as WindowHolder).showSnackbar(snackbar)
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