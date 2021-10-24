package com.theost.workchat.ui.activity

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
import com.theost.workchat.data.models.ChannelsType
import com.theost.workchat.databinding.FragmentStreamsBinding
import com.theost.workchat.ui.widgets.NavigationHolder
import com.theost.workchat.ui.widgets.StreamsAdapter
import com.theost.workchat.utils.DisplayUtils

class StreamsFragment : Fragment() {

    private var _binding: FragmentStreamsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentStreamsBinding.inflate(layoutInflater)
        configureToolbar()

        val channelsPages = binding.root.findViewById<ViewPager2>(R.id.channelsPages)
        val channelsTabs = binding.root.findViewById<TabLayout>(R.id.channelsTabs)
        val channelsTypes = ChannelsType.values()
        channelsPages.adapter = StreamsAdapter(requireActivity(), channelsTypes)
        TabLayoutMediator(channelsTabs, channelsPages) { tab, position ->
            tab.text = channelsTypes[position].uiName
        }.attach()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun configureToolbar() {
        (activity as NavigationHolder).showNavigation()
        binding.toolbarLayout.toolbar.title = getString(R.string.channels)
        val searchMenuItem = binding.toolbarLayout.toolbar.menu.findItem(R.id.actionSearch)
        val searchView = searchMenuItem.actionView as SearchView
        val searchManager = context?.getSystemService(SEARCH_SERVICE) as SearchManager
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

    private fun onQueryChanged(query: String) {
        // viewModel.filterData(query)
    }

    companion object {
        fun newFragment(): Fragment {
            return StreamsFragment()
        }
    }

}