package com.theost.workchat.ui.fragments

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.theost.workchat.R
import com.theost.workchat.data.models.state.ChannelsType
import com.theost.workchat.databinding.FragmentStreamsBinding
import com.theost.workchat.ui.adapters.core.StreamsAdapter
import com.theost.workchat.ui.interfaces.NavigationHolder
import com.theost.workchat.ui.interfaces.SearchHandler
import com.theost.workchat.ui.interfaces.TopicListener
import com.theost.workchat.utils.DisplayUtils

class StreamsFragment : Fragment() {

    private lateinit var channelsPages: ViewPager2
    private lateinit var channelsPagesCallback: ViewPager2.OnPageChangeCallback
    private lateinit var searchView: SearchView

    private var currentPagesPosition: Int = -1

    private var _binding: FragmentStreamsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentStreamsBinding.inflate(layoutInflater)

        val channelsTypes = ChannelsType.values()
        val channelsTabs = binding.root.findViewById<TabLayout>(R.id.channelsTabs)
        channelsPages = binding.root.findViewById(R.id.channelsPages)

        channelsPages.adapter = StreamsAdapter(childFragmentManager, lifecycle, channelsTypes)

        channelsPagesCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (searchView.hasFocus()) searchView.setQuery("", false)
                currentPagesPosition = channelsPages.currentItem
            }
        }
        channelsPages.registerOnPageChangeCallback(channelsPagesCallback)
        currentPagesPosition = channelsPages.currentItem

        TabLayoutMediator(channelsTabs, channelsPages) { tab, position ->
            tab.text = channelsTypes[position].uiName
        }.attach()

        binding.createButton.setOnClickListener {
            activity?.let { activity -> (activity as TopicListener).createChannel() }
        }

        configureToolbar()

        return binding.root
    }

    private fun onQueryChanged(query: String) {
        childFragmentManager.findFragmentByTag("f$currentPagesPosition").let { fragment ->
            (fragment as? SearchHandler)?.onSearch(query)
        }
    }

    private fun configureToolbar() {
        activity?.let { activity -> (activity as NavigationHolder).showNavigation() }
        binding.toolbarLayout.toolbar.title = getString(R.string.channels)
        val searchMenuItem = binding.toolbarLayout.toolbar.menu.findItem(R.id.actionSearch)
        val searchManager = context?.getSystemService(SEARCH_SERVICE) as SearchManager
        searchView = searchMenuItem.actionView as SearchView
        searchMenuItem.isVisible = true
        searchView.apply {
            findViewById<ImageView>(R.id.search_close_btn).setImageResource(R.drawable.ic_close)
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            setIconifiedByDefault(false)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    DisplayUtils.hideKeyboard(activity)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    onQueryChanged(newText)
                    return true
                }

            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        channelsPages.unregisterOnPageChangeCallback(channelsPagesCallback)
        channelsPages.adapter = null
        searchView.setOnQueryTextListener(null)
        _binding = null
    }

    companion object {
        fun newFragment(): Fragment {
            return StreamsFragment()
        }
    }

}