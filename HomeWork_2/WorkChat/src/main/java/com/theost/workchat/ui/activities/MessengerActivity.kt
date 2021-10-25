package com.theost.workchat.ui.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
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

    private var navigationPrevSelectedId = 0
    private lateinit var binding: ActivityMessengerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessengerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener {
            if (it.itemId != navigationPrevSelectedId) {
                navigationPrevSelectedId = it.itemId
                when (it.itemId) {
                    R.id.navChannels -> navigateFragment(StreamsFragment.newFragment())
                    R.id.navPeople -> navigateFragment(PeopleFragment.newFragment())
                    R.id.navProfile -> navigateFragment(ProfileFragment.newFragment(0))
                }
            }
            true
        }
        binding.bottomNavigation.selectedItemId = R.id.navChannels
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

    override fun showNavigation() {
        binding.bottomNavigation.animate()
            .withStartAction { binding.bottomNavigation.visibility = View.VISIBLE }
            .translationY(0f).duration = 150
    }

    override fun hideNavigation() {
        binding.bottomNavigation.animate()
            .withEndAction { binding.bottomNavigation.visibility = View.GONE }
            .translationY(binding.bottomNavigation.height.toFloat()).duration = 150
    }

    override fun onProfileSelected(profileId: Int) {
        startFragment(ProfileFragment.newFragment(profileId))
    }

    override fun showTopicDialog(topicId: Int) {
        startFragment(DialogFragment.newFragment(topicId))
    }

    private fun navigateFragment(fragment: Fragment) {
        DisplayUtils.hideKeyboard(this)

        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
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
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

}