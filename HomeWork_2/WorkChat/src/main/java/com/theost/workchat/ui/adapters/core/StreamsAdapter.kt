package com.theost.workchat.ui.adapters.core

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.ui.fragments.ChannelsFragment

class StreamsAdapter(
    fragmentActivity: FragmentActivity,
    private val channelsTypes: Array<ChannelsType>
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment = ChannelsFragment.newFragment(channelsTypes[position])

    override fun getItemCount(): Int = ChannelsType.values().size
}