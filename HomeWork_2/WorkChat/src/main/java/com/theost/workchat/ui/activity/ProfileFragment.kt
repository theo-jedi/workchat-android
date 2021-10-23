package com.theost.workchat.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.theost.workchat.R
import com.theost.workchat.databinding.FragmentProfileBinding

class ProfileFragment :  Fragment() {

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

        return binding.root
    }

    private fun configureToolbar() {
        binding.toolbarLayout.toolbar.title = getString(R.string.profile)
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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