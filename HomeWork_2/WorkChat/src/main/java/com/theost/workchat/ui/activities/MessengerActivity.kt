package com.theost.workchat.ui.activities

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.theost.workchat.R
import com.theost.workchat.databinding.ActivityMessengerBinding
import com.theost.workchat.ui.fragments.DialogFragment
import com.theost.workchat.ui.fragments.PeopleFragment
import com.theost.workchat.ui.fragments.ProfileFragment
import com.theost.workchat.ui.fragments.StreamsFragment
import com.theost.workchat.ui.interfaces.NavigationHolder
import com.theost.workchat.ui.interfaces.PeopleListener
import com.theost.workchat.ui.interfaces.TopicListener
import com.theost.workchat.utils.DisplayUtils

class MessengerActivity : FragmentActivity(), NavigationHolder, PeopleListener, TopicListener {

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
    }

    override fun onBackPressed() {
        DisplayUtils.hideKeyboard(this)

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

    override fun onProfileSelected(profileId: Int) {
        startFragment(ProfileFragment.newFragment(profileId))
    }

    override fun showTopicDialog(channelName: String, topicName: String) {
        startFragment(DialogFragment.newFragment(channelName, topicName))
    }

    private fun navigateFragment(fragment: Fragment, tag: String) {
        DisplayUtils.hideKeyboard(this)

        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            if (supportFragmentManager.backStackEntryCount > 0) supportFragmentManager.popBackStack()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .addToBackStack(tag)
                .commit()
        }
    }

    private fun startFragment(fragment: Fragment) {
        DisplayUtils.hideKeyboard(this)
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