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
import androidx.fragment.app.viewModels
import com.theost.workchat.R
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.databinding.FragmentPeopleBinding
import com.theost.workchat.ui.viewmodels.PeopleViewModel
import com.theost.workchat.ui.adapters.core.BaseAdapter
import com.theost.workchat.ui.interfaces.NavigationHolder
import com.theost.workchat.ui.adapters.delegates.PeopleAdapterDelegate
import com.theost.workchat.ui.interfaces.PeopleListener
import com.theost.workchat.utils.DisplayUtils

class PeopleFragment : Fragment() {

    private var userId: Int = 0
    private var usersList: List<ListUser> = listOf()
    private val adapter = BaseAdapter()

    private val viewModel: PeopleViewModel by viewModels()

    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentPeopleBinding.inflate(layoutInflater)
        configureToolbar()

        binding.usersList.adapter = adapter.apply {
            addDelegate(PeopleAdapterDelegate() { profileId ->
                (activity as PeopleListener).onProfileSelected(profileId)
            })
        }

        viewModel.allData.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.loadData(userId)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun configureToolbar() {
        (activity as NavigationHolder).showNavigation()
        binding.toolbarLayout.toolbar.title = getString(R.string.people)
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
        viewModel.filterData(query)
    }

    companion object {
        fun newFragment(): Fragment {
            val fragment = PeopleFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

}