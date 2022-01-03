package com.theost.workchat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.theost.workchat.R
import com.theost.workchat.application.WorkChatApp
import com.theost.workchat.databinding.FragmentCreationTopicBinding
import com.theost.workchat.di.ui.DaggerCreationTopicComponent
import com.theost.workchat.elm.creation.topic.*
import com.theost.workchat.ui.interfaces.TopicListener
import com.theost.workchat.ui.interfaces.WindowHolder
import com.theost.workchat.utils.DisplayUtils
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class CreationTopicFragment :
    ElmFragment<CreationTopicEvent, CreationTopicEffect, CreationTopicState>() {

    @Inject
    lateinit var actor: CreationTopicActor

    private var _binding: FragmentCreationTopicBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentCreationTopicBinding.inflate(layoutInflater)
        configureToolbar()

        binding.topicInputLayout.editText?.addTextChangedListener { editable ->
            store.accept(
                CreationTopicEvent.Ui.OnInputTextChanged(
                    editable.toString().trim(),
                    binding.messageInputLayout.editText?.text.toString().trim()
                )
            )
        }

        binding.messageInputLayout.editText?.addTextChangedListener { editable ->
            store.accept(
                CreationTopicEvent.Ui.OnInputTextChanged(
                    editable.toString().trim(),
                    binding.topicInputLayout.editText?.text.toString().trim()
                )
            )

        }

        binding.submitButton.setOnClickListener {
            store.accept(
                CreationTopicEvent.Ui.OnSubmitClicked(
                    binding.topicInputLayout.editText?.text.toString().trim(),
                    binding.messageInputLayout.editText?.text.toString().trim()
                )
            )
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerCreationTopicComponent.factory().create(WorkChatApp.appComponent).inject(this)
    }

    override val initEvent: CreationTopicEvent = CreationTopicEvent.Ui.Init

    override fun createStore(): Store<CreationTopicEvent, CreationTopicEffect, CreationTopicState> {
        return CreationTopicStore.getStore(
            actor,
            CreationTopicState(
                channelName = arguments?.getString(CHANNEL_NAME_EXTRA) ?: "",
                channelDescription = arguments?.getString(CHANNEL_DESCRIPTION_EXTRA) ?: ""
            )
        )
    }

    override fun render(state: CreationTopicState) {}

    override fun handleEffect(effect: CreationTopicEffect) {
        when (effect) {
            CreationTopicEffect.ShowLoading -> showSendingLoading()
            CreationTopicEffect.HideLoading -> hideSendingLoading()
            CreationTopicEffect.ShowError -> showSendingError()
            CreationTopicEffect.HideKeyboard -> hideKeyboard()
            CreationTopicEffect.EnableSubmitButton -> enableSubmitButton()
            CreationTopicEffect.DisableSubmitButton -> disableSubmitButton()
            CreationTopicEffect.OpenChannels -> openChannels()
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

    private fun enableSubmitButton() {
        binding.submitButton.isEnabled = true
    }

    private fun disableSubmitButton() {
        binding.submitButton.isEnabled = false
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

    private fun configureToolbar() {
        binding.toolbarLayout.toolbar.title = getString(R.string.topic)
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
        private const val CHANNEL_NAME_EXTRA = "channel_name"
        private const val CHANNEL_DESCRIPTION_EXTRA = "channel_description"

        fun newFragment(channelName: String, channelDescription: String): Fragment {
            val fragment = CreationTopicFragment()
            val bundle = Bundle()
            bundle.putString(CHANNEL_NAME_EXTRA, channelName)
            bundle.putString(CHANNEL_DESCRIPTION_EXTRA, channelDescription)
            fragment.arguments = bundle
            return fragment
        }
    }

}