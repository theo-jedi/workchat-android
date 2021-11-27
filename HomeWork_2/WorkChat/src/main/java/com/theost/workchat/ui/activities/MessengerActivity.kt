package com.theost.workchat.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.databinding.ActivityMessengerBinding
import com.theost.workchat.ui.fragments.DialogFragment
import com.theost.workchat.ui.fragments.PeopleFragment
import com.theost.workchat.ui.fragments.ProfileFragment
import com.theost.workchat.ui.fragments.StreamsFragment
import com.theost.workchat.ui.interfaces.NavigationHolder
import com.theost.workchat.ui.interfaces.PeopleListener
import com.theost.workchat.ui.interfaces.TopicListener
import com.theost.workchat.ui.interfaces.WindowHolder
import com.theost.workchat.ui.viewmodels.MessengerViewModel
import com.theost.workchat.utils.DisplayUtils
import com.theost.workchat.utils.PrefUtils

class MessengerActivity : FragmentActivity(), WindowHolder, NavigationHolder, PeopleListener,
    TopicListener {

    private val viewModel: MessengerViewModel by viewModels()
    private var snackbar: Snackbar? = null

    private lateinit var binding: ActivityMessengerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessengerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener {
            onNavigationItemSelected(it.itemId)
            true
        }
        binding.bottomNavigation.selectedItemId = R.id.navChannels

        viewModel.currentUserId.observe(this) { userId ->
            PrefUtils.putCurrentUserId(
                context = this,
                userId = userId
            )
        }
    }

    override fun onBackPressed() {
        hideFloatingViews()

        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

    private fun onNavigationItemSelected(itemId: Int) {
        when (itemId) {
            R.id.navChannels -> navigateFragment(StreamsFragment.newFragment(), "channels")
            R.id.navPeople -> navigateFragment(PeopleFragment.newFragment(), "people")
            R.id.navProfile -> navigateFragment(ProfileFragment.newFragment(), "profile")
        }
    }

    private fun hideFloatingViews() {
        DisplayUtils.hideKeyboard(this)
        hideSnackbar()
    }

    override fun showSnackbar(snackbar: Snackbar, view: View?) {
        this.snackbar = snackbar.apply {
            anchorView = view ?: binding.bottomNavigation
            show()
        }
    }

    override fun hideSnackbar() {
        this.snackbar?.dismiss()
    }

    override fun showNavigation() {
        if (!binding.bottomNavigation.isVisible) binding.bottomNavigation.animate()
            .withStartAction { binding.bottomNavigation.visibility = View.VISIBLE }
            .translationY(0f).duration = 150
    }

    override fun hideNavigation() {
        if (binding.bottomNavigation.isVisible) binding.bottomNavigation.animate()
            .withEndAction { binding.bottomNavigation.visibility = View.GONE }
            .translationY(binding.bottomNavigation.height.toFloat()).duration = 150
    }

    override fun onProfileSelected(userId: Int) {
        startFragment(ProfileFragment.newFragment(userId))
    }

    override fun showTopicDialog(channelName: String, topicName: String) {
        startFragment(DialogFragment.newFragment(channelName, topicName))
    }

    private fun updateCurrentUser() {
        if (PrefUtils.getCurrentUserId(this) == 0) {
            viewModel.updateData()
        }
    }

    private fun navigateFragment(fragment: Fragment, tag: String) {
        updateCurrentUser() // todo replace with network listener
        hideFloatingViews()

        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            if (supportFragmentManager.backStackEntryCount > 0) supportFragmentManager.popBackStack()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .addToBackStack(tag)
                .commit()
        }
    }

    private fun startFragment(fragment: Fragment) {
        hideFloatingViews()
        hideNavigation()

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_from_right,
                R.anim.slide_to_left,
                R.anim.slide_from_left,
                R.anim.slide_to_right
            )
            .replace(R.id.fragmentContainer, fragment, null)
            .addToBackStack(null)
            .commit()
    }

}