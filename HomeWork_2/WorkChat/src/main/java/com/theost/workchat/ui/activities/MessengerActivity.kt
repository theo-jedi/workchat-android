package com.theost.workchat.ui.activities

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.databinding.ActivityMessengerBinding
import com.theost.workchat.di.ui.DaggerMessengerComponent
import com.theost.workchat.elm.messenger.*
import com.theost.workchat.ui.fragments.DialogFragment
import com.theost.workchat.ui.fragments.PeopleFragment
import com.theost.workchat.ui.fragments.ProfileFragment
import com.theost.workchat.ui.fragments.StreamsFragment
import com.theost.workchat.ui.interfaces.NavigationHolder
import com.theost.workchat.ui.interfaces.PeopleListener
import com.theost.workchat.ui.interfaces.TopicListener
import com.theost.workchat.ui.interfaces.WindowHolder
import com.theost.workchat.utils.DisplayUtils
import com.theost.workchat.utils.PrefUtils
import vivid.money.elmslie.android.base.ElmActivity
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class MessengerActivity : ElmActivity<MessengerEvent, MessengerEffect, MessengerState>(),
    WindowHolder, NavigationHolder, PeopleListener, TopicListener {

    @Inject
    lateinit var actor: MessengerActor

    private var snackbar: Snackbar? = null
    private lateinit var binding: ActivityMessengerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessengerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            store.accept(MessengerEvent.Ui.OnNavigationClick(menuItem.itemId))
            true
        }

        DaggerMessengerComponent.factory().create(WorkChatApp.appComponent).inject(this)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

    override val initEvent: MessengerEvent = MessengerEvent.Ui.Init(R.id.navChannels)

    override fun createStore(): Store<MessengerEvent, MessengerEffect, MessengerState> {
        return MessengerStore.getStore(
            actor,
            MessengerState(currentUserId = PrefUtils.getCurrentUserId(this))
        )
    }

    override fun render(state: MessengerState) {
        if (state.currentUserId != -1) {
            PrefUtils.putCurrentUserId(
                context = this,
                userId = state.currentUserId
            )
        }
    }

    override fun handleEffect(effect: MessengerEffect) {
        when (effect) {
            is MessengerEffect.SelectNavigation -> selectNavigation(effect.itemId)
            is MessengerEffect.HideNavigation -> hideNavigation()
            is MessengerEffect.ShowNavigation -> showNavigation()
            is MessengerEffect.HideFloatingViews -> hideFloatingViews()
            is MessengerEffect.NavigateStreams -> navigateFragment(StreamsFragment.newFragment(), FRAGMENT_CHANNELS_TAG)
            is MessengerEffect.NavigatePeople -> navigateFragment(PeopleFragment.newFragment(), FRAGMENT_PEOPLE_TAG)
            is MessengerEffect.NavigateProfile -> navigateFragment(ProfileFragment.newFragment(), FRAGMENT_PROFILE_TAG)
            is MessengerEffect.OpenProfile -> startFragment(ProfileFragment.newFragment(effect.userId))
            is MessengerEffect.OpenDialog -> startFragment(
                DialogFragment.newFragment(
                    effect.channelName,
                    effect.topicName
                )
            )
        }
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

    private fun selectNavigation(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
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

    override fun openProfile(userId: Int) {
        store.accept(MessengerEvent.Ui.OnProfileClick(userId))
    }

    override fun openDialog(channelName: String, topicName: String) {
        store.accept(MessengerEvent.Ui.OnDialogClick(channelName, topicName))
    }

    private fun hideFloatingViews() {
        DisplayUtils.hideKeyboard(this)
        hideSnackbar()
    }

    private fun navigateFragment(fragment: Fragment, tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            if (supportFragmentManager.backStackEntryCount > 0) supportFragmentManager.popBackStack()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .addToBackStack(tag)
                .commit()
        }
    }

    private fun startFragment(fragment: Fragment) {
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

    companion object {
        private const val FRAGMENT_CHANNELS_TAG = "channels"
        private const val FRAGMENT_PEOPLE_TAG = "people"
        private const val FRAGMENT_PROFILE_TAG = "profile"
    }

}