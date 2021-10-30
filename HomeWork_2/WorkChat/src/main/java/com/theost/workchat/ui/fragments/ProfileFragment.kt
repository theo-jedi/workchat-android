package com.theost.workchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.theost.workchat.R
import com.theost.workchat.data.models.core.User
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.databinding.FragmentProfileBinding
import com.theost.workchat.ui.interfaces.NavigationHolder
import com.theost.workchat.ui.viewmodels.ProfileViewModel

class ProfileFragment : Fragment() {

    private var userId: Int = 0
    private var profileId: Int = 0

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
                ResourceStatus.ERROR -> { /* todo */ }
                ResourceStatus.LOADING ->  {}
                else -> {}
            }
        }
        viewModel.allData.observe(viewLifecycleOwner) { setData(it) }
        viewModel.loadData(profileId)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileId = savedInstanceState?.getInt(PROFILE_ID_EXTRA)
            ?: (arguments?.getInt(PROFILE_ID_EXTRA) ?: 0)
    }

    private fun configureLayout() {
        if (profileId == userId) {
            binding.userLogout.visibility = View.VISIBLE
        }
    }

    private fun configureToolbar() {
        binding.toolbarLayout.toolbar.title = getString(R.string.profile)
        if (profileId != userId) {
            binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_back)
            binding.toolbarLayout.toolbar.setNavigationOnClickListener {
                activity?.onBackPressed()
            }
        } else {
            (activity as NavigationHolder).showNavigation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setData(user: User?) {
        binding.userName.text = user?.name
        binding.userAbout.text = user?.about
        binding.userAvatar.setImageResource(user?.avatar ?: R.mipmap.sample_avatar)
        binding.userStatus.visibility = if (user?.status == true) View.VISIBLE else View.INVISIBLE
    }

    private fun hideShimmerLayout() {
        binding.shimmerLayout.shimmer.visibility = View.GONE
        binding.avatarLayout.visibility = View.VISIBLE
        binding.userName.visibility = View.VISIBLE
        binding.userAbout.visibility = View.VISIBLE
        binding.userStatus.visibility = View.VISIBLE

        configureLayout()
    }

    companion object {
        private const val PROFILE_ID_EXTRA = "profile_id"

        fun newFragment(profileId: Int): Fragment {
            val fragment = ProfileFragment()
            val bundle = Bundle()
            bundle.putInt(PROFILE_ID_EXTRA, profileId)
            fragment.arguments = bundle
            return fragment
        }
    }

}