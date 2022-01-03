package com.theost.workchat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.data.models.state.ResourceStatus
import com.theost.workchat.databinding.FragmentCreationChannelBinding
import com.theost.workchat.di.ui.DaggerCreationChannelComponent
import com.theost.workchat.elm.creation.channel.*
import com.theost.workchat.ui.interfaces.TopicListener
import com.theost.workchat.ui.interfaces.WindowHolder
import com.theost.workchat.utils.DisplayUtils
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class CreationChannelFragment :
    ElmFragment<CreationChannelEvent, CreationChannelEffect, CreationChannelState>() {

    @Inject
    lateinit var actor: CreationChannelActor

    private val dropdownAdapter: ArrayAdapter<String>? by lazy {
        context?.let { context ->
            ArrayAdapter(
                context,
                R.layout.item_dropdown,
                mutableListOf<String>()
            )
        }
    }

    private var isDropdownLocked = false

    private var _binding: FragmentCreationChannelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentCreationChannelBinding.inflate(layoutInflater)
        configureToolbar()

        (binding.channelInputLayout.editText as AutoCompleteTextView).apply {
            context?.let { context ->
                setDropDownBackgroundDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_dropdown
                    )
                )
            }
            dropdownAdapter?.let { setAdapter(dropdownAdapter) }
            addTextChangedListener { editable ->
                if (isDropdownLocked) (binding.channelInputLayout.editText as AutoCompleteTextView).dismissDropDown()
                store.accept(CreationChannelEvent.Ui.OnInputTextChanged(editable.toString().trim()))
            }
        }

        binding.submitButton.setOnClickListener { submitChannel() }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerCreationChannelComponent.factory().create(WorkChatApp.appComponent).inject(this)
    }

    override val initEvent: CreationChannelEvent = CreationChannelEvent.Ui.LoadChannels

    override fun createStore(): Store<CreationChannelEvent, CreationChannelEffect, CreationChannelState> {
        return CreationChannelStore.getStore(actor, CreationChannelState())
    }

    override fun render(state: CreationChannelState) {
        if (
            state.stateStatus == ResourceStatus.LOADING
            && state.status == ResourceStatus.SUCCESS
            && binding.channelInputLayout.editText?.text?.isEmpty() == true
        ) {
            dropdownAdapter?.clear()
            dropdownAdapter?.addAll(state.channels)

            binding.channelInputLayout.editText?.setText(state.channelName)
            binding.descriptionInputLayout.editText?.setText(state.channelDescription)

            store.accept(CreationChannelEvent.Ui.OnStateRestored)
        }
    }

    override fun handleEffect(effect: CreationChannelEffect) {
        when (effect) {
            is CreationChannelEffect.ShowSendingLoading -> showSendingLoading()
            is CreationChannelEffect.HideSendingLoading -> hideSendingLoading()
            is CreationChannelEffect.ShowLoadingError -> showLoadingError()
            is CreationChannelEffect.ShowSendingError -> showSendingError()
            is CreationChannelEffect.EnableSubmitButton -> enableSubmitButton()
            is CreationChannelEffect.DisableSubmitButton -> disableSubmitButton()
            is CreationChannelEffect.HideKeyboard -> hideKeyboard()
            is CreationChannelEffect.SwitchChannelCreation -> switchChannelCreation()
            is CreationChannelEffect.SwitchTopicCreation -> switchTopicCreation()
            is CreationChannelEffect.OpenChannels -> openChannels()
            is CreationChannelEffect.OpenCreationTopic -> openCreationTopic(
                effect.channelName,
                effect.channelDescription
            )
        }
    }

    private fun showSendingLoading() {
        binding.submitButton.visibility = View.INVISIBLE
        binding.loadingBar.visibility = View.VISIBLE
    }

    private fun hideSendingLoading() {
        binding.submitButton.visibility = View.VISIBLE
        binding.loadingBar.visibility = View.GONE
    }

    private fun openChannels() {
        activity?.let { activity -> (activity as TopicListener).openChannels() }
    }

    private fun submitChannel() {
        val channelName = binding.channelInputLayout.editText?.text.toString().trim()
        val channelDescription = if (binding.descriptionInputLayout.visibility == View.VISIBLE) {
            binding.descriptionInputLayout.editText?.text.toString().trim()
        } else ""
        store.accept(CreationChannelEvent.Ui.OnSubmitClicked(channelName, channelDescription))
    }

    private fun openCreationTopic(channelName: String, channelDescription: String) {
        (activity as TopicListener).createTopic(channelName, channelDescription)
    }

    private fun enableSubmitButton() {
        binding.submitButton.isEnabled = true
    }

    private fun disableSubmitButton() {
        binding.submitButton.isEnabled = false
    }

    private fun switchChannelCreation() {
        binding.submitButton.text = getString(R.string.create)
        binding.descriptionInputLayout.animate().alpha(1f)
            .withStartAction { binding.descriptionInputLayout.visibility = View.VISIBLE }
            .withEndAction {
                isDropdownLocked = false
                binding.channelInputLayout.editText?.requestFocus()
            }
            .apply { duration = 0 }
    }

    private fun switchTopicCreation() {
        binding.submitButton.text = getString(R.string.next)
        binding.descriptionInputLayout.animate().alpha(0f)
            .withStartAction { isDropdownLocked = true }
            .withEndAction { binding.descriptionInputLayout.visibility = View.GONE }
            .apply { duration = 0 }
    }

    private fun hideKeyboard() {
        activity?.let { DisplayUtils.hideKeyboard(activity) }
    }

    private fun showSendingError() {
        activity?.let { activity ->
            val snackbar = Snackbar.make(
                binding.root,
                getString(R.string.network_error),
                Snackbar.LENGTH_INDEFINITE
            ).apply { setAction(R.string.hide) { dismiss() } }
            (activity as WindowHolder).showSnackbar(snackbar)
        }
    }

    private fun showLoadingError() {
        activity?.let { activity ->
            val snackbar = Snackbar.make(
                binding.root,
                getString(R.string.database_error),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.retry) { store.accept(CreationChannelEvent.Ui.LoadChannels) }
            (activity as WindowHolder).showSnackbar(snackbar)
        }
    }

    private fun configureToolbar() {
        binding.toolbarLayout.toolbar.title = getString(R.string.channel)
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
        fun newFragment(): Fragment {
            val fragment = CreationChannelFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

}