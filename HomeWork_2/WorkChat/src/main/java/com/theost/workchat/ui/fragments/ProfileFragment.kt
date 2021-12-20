package com.theost.workchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.data.models.ui.ListUser
import com.theost.workchat.databinding.FragmentProfileBinding
import com.theost.workchat.ui.interfaces.NavigationHolder
import com.theost.workchat.ui.viewmodels.ProfileViewModel

class ProfileFragment : Fragment() {

    private var userId: Int = -1

    private val viewModel: ProfileViewModel by viewModels()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentProfileBinding.inflate(layoutInflater)

        configureToolbar()

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                ResourceStatus.SUCCESS -> hideShimmerLayout()
                ResourceStatus.ERROR -> { showLoadingError() }
                ResourceStatus.LOADING ->  {}
                else -> {}
            }
        }
        viewModel.allData.observe(viewLifecycleOwner) { setData(it) }
        loadData()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = savedInstanceState?.getInt(PROFILE_ID_EXTRA)
            ?: (arguments?.getInt(PROFILE_ID_EXTRA) ?: -1)
    }

    private fun configureToolbar() {
        binding.toolbarLayout.toolbar.title = getString(R.string.profile)
        if (userId != -1) {
            binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_back)
            binding.toolbarLayout.toolbar.setNavigationOnClickListener {
                activity?.onBackPressed()
            }
        } else {
            (activity as NavigationHolder).showNavigation()
        }
    }

    private fun loadData() {
        viewModel.loadData(userId)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setData(user: ListUser) {
        Glide.with(this).load(user.avatarUrl).into(binding.userAvatar)

        binding.userName.text = user.name
        binding.userAbout.text = user.about

        when (user.status) {
            UserStatus.ONLINE -> binding.userStatusOnline.visibility = View.VISIBLE
            UserStatus.IDLE -> binding.userStatusIdle.visibility = View.VISIBLE
            else -> {}
        }
    }

    private fun hideShimmerLayout() {
        binding.shimmerLayout.shimmer.visibility = View.GONE
        binding.avatarLayout.visibility = View.VISIBLE
        binding.userName.visibility = View.VISIBLE
        binding.userAbout.visibility = View.VISIBLE
        //binding.userStatus.visibility = if () View.VISIBLE else View.INVISIBLE
    }

    private fun showLoadingError() {
        Snackbar.make(binding.root, getString(R.string.network_error), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) { loadData() }
            .show()
    }

    companion object {
        private const val PROFILE_ID_EXTRA = "profile_id"

        fun newFragment(userId: Int = -1): Fragment {
            val fragment = ProfileFragment()
            val bundle = Bundle()
            bundle.putInt(PROFILE_ID_EXTRA, userId)
            fragment.arguments = bundle
            return fragment
        }
    }

}