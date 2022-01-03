package com.theost.workchat.ui.adapters.core

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.ui.fragments.ChannelsFragment

class StreamsAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val channelsTypes: Array<ChannelsType>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment = ChannelsFragment.newFragment(channelsTypes[position])

    override fun getItemCount(): Int = ChannelsType.values().size
}