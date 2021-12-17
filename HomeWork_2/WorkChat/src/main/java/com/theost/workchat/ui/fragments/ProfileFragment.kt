package com.theost.workchat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.state.UserStatus
import com.theost.workchat.databinding.FragmentProfileBinding
import com.theost.workchat.di.ui.DaggerProfileComponent
import com.theost.workchat.elm.profile.*
import com.theost.workchat.ui.interfaces.NavigationHolder
import com.theost.workchat.ui.interfaces.WindowHolder
import com.theost.workchat.utils.PrefUtils
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class ProfileFragment : ElmFragment<ProfileEvent, ProfileEffect, ProfileState>() {

    @Inject
    lateinit var actor: ProfileActor

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentProfileBinding.inflate(layoutInflater)

        binding.toolbarLayout.toolbar.title = getString(R.string.profile)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerProfileComponent.factory().create(WorkChatApp.appComponent).inject(this)
    }

    override val initEvent: ProfileEvent = ProfileEvent.Ui.LoadProfile

    override fun createStore(): Store<ProfileEvent, ProfileEffect, ProfileState> {
        var userId = arguments?.getInt(PROFILE_ID_EXTRA) ?: -1
        var isCurrentUser = false

        if (userId == -1) {
            context?.let { userId = PrefUtils.getCurrentUserId(it) }
            isCurrentUser = true
        }

        return ProfileStore.getStore(
            actor,
            ProfileState(
                userId = userId,
                isCurrentUser = isCurrentUser
            )
        )
    }

    override fun render(state: ProfileState) {
        if (state.profile != null) {
            val user = state.profile

            Glide.with(this)
                .load(user.avatarUrl)
                .placeholder(R.drawable.ic_avatar_loading)
                .error(R.drawable.ic_avatar_error)
                .into(binding.userAvatar)

            binding.userName.text = user.name
            binding.userAbout.text = user.about

            when (user.status) {
                UserStatus.ONLINE -> showStatus(binding.userStatusOnline)
                UserStatus.IDLE -> showStatus(binding.userStatusIdle)
                UserStatus.OFFLINE -> {
                    hideStatus(binding.userStatusOnline)
                    hideStatus(binding.userStatusIdle)
                }
            }
        }

        configureToolbar(state.isCurrentUser)
    }

    override fun handleEffect(effect: ProfileEffect) {
        when (effect) {
            is ProfileEffect.ShowError -> showLoadingError()
            is ProfileEffect.ShowLoading -> showLoading()
            is ProfileEffect.HideLoading -> hideLoading()
        }
    }

    private fun showStatus(statusView: View) {
        statusView.visibility = View.VISIBLE
        statusView.alpha = 0f
        statusView.animate().alpha(1f)
    }

    private fun hideStatus(statusView: View) {
        statusView.animate().alpha(0f).withEndAction { statusView.visibility = View.GONE }
    }

    private fun hideLoading() {
        binding.shimmerLayout.shimmer.visibility = View.GONE
        binding.avatarLayout.visibility = View.VISIBLE
        binding.userName.visibility = View.VISIBLE
        binding.userAbout.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.shimmerLayout.shimmer.visibility = View.VISIBLE
        binding.avatarLayout.visibility = View.GONE
        binding.userName.visibility = View.GONE
        binding.userAbout.visibility = View.GONE
    }

    private fun showLoadingError() {
        activity?.let { activity ->
            val snackbar = Snackbar.make(
                binding.root,
                getString(R.string.network_error),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.retry) { store.accept(ProfileEvent.Ui.LoadProfile) }
            (activity as WindowHolder).showSnackbar(snackbar)
        }
    }

    private fun configureToolbar(isCurrentUser: Boolean) {
        if (!isCurrentUser) {
            binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_back)
            binding.toolbarLayout.toolbar.setNavigationOnClickListener {
                activity?.onBackPressed()
            }
        } else {
            activity?.let { activity -> (activity as NavigationHolder).showNavigation() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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