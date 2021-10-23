package com.theost.workchat.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.theost.workchat.R
import com.theost.workchat.databinding.FragmentProfileBinding
import com.theost.workchat.ui.widgets.ToolbarHolder

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
        activity?.let { activity ->
            val toolbarHolder = activity as ToolbarHolder
            toolbarHolder.setToolbarTitle(getString(R.string.profile))
            toolbarHolder.showToolbar()
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