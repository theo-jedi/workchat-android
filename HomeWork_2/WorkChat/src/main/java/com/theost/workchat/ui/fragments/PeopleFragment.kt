package com.theost.workchat.ui.fragments

import android.app.SearchManager
import android.content.Context
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.databinding.FragmentPeopleBinding
import com.theost.workchat.di.ui.DaggerPeopleComponent
import com.theost.workchat.elm.people.*
import com.theost.workchat.ui.adapters.core.BaseAdapter
import com.theost.workchat.ui.adapters.delegates.PeopleAdapterDelegate
import com.theost.workchat.ui.interfaces.NavigationHolder
import com.theost.workchat.ui.interfaces.PeopleListener
import com.theost.workchat.ui.interfaces.WindowHolder
import com.theost.workchat.utils.DisplayUtils
import com.theost.workchat.utils.PrefUtils
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class PeopleFragment : ElmFragment<PeopleEvent, PeopleEffect, PeopleState>() {

    @Inject
    lateinit var actor: PeopleActor

    private lateinit var searchView: SearchView
    private val adapter: BaseAdapter = BaseAdapter()

    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentPeopleBinding.inflate(layoutInflater)

        binding.emptyLayout.emptyView.text = getString(R.string.no_people)

        binding.usersList.setHasFixedSize(true)
        binding.usersList.adapter = adapter.apply {
            addDelegate(PeopleAdapterDelegate { userId ->
                activity?.let { activity -> (activity as PeopleListener).openProfile(userId) }
            })
        }

        configureToolbar()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerPeopleComponent.factory().create(WorkChatApp.appComponent).inject(this)
    }

    override val initEvent: PeopleEvent = PeopleEvent.Ui.LoadPeople

    override fun createStore(): Store<PeopleEvent, PeopleEffect, PeopleState> {
        return PeopleStore.getStore(
            actor,
            PeopleState(
                currentUserId = context?.let { PrefUtils.getCurrentUserId(it) } ?: -1
            )
        )
    }

    override fun render(state: PeopleState) {
        val people = if (state.isSearchEnabled) state.searchedPeople else state.people
        adapter.submitList(people)
    }

    override fun handleEffect(effect: PeopleEffect) {
        when (effect) {
            is PeopleEffect.ShowError -> showLoadingError()
            is PeopleEffect.ShowLoading -> showLoading()
            is PeopleEffect.HideLoading -> hideLoading()
            is PeopleEffect.ShowEmpty -> showEmptyView()
            is PeopleEffect.HideEmpty -> hideEmptyView()
            is PeopleEffect.OpenProfile -> openProfile(effect.userId)
        }
    }

    fun onSearch(query: String) {
        store.accept(PeopleEvent.Ui.SearchPeople(query))
    }

    private fun openProfile(userId: Int) {
        activity?.let { activity -> (activity as PeopleListener).openProfile(userId) }
    }

    private fun hideEmptyView() {
        binding.emptyLayout.emptyView.visibility = View.GONE
    }

    private fun showEmptyView() {
        binding.emptyLayout.emptyView.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.shimmerLayout.shimmer.visibility = View.GONE
        binding.usersList.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.shimmerLayout.shimmer.visibility = View.VISIBLE
        binding.usersList.visibility = View.GONE
    }

    private fun showLoadingError() {
        activity?.let { activity ->
            val snackbar = Snackbar.make(
                binding.root,
                getString(R.string.network_error),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.retry) { store.accept(PeopleEvent.Ui.LoadPeople) }
            (activity as WindowHolder).showSnackbar(snackbar)
        }
    }

    private fun configureToolbar() {
        activity?.let { activity -> (activity as NavigationHolder).showNavigation() }
        binding.toolbarLayout.toolbar.title = getString(R.string.people)
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
                    onSearch(newText)
                    return true
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchView.setOnQueryTextListener(null)
        _binding = null
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