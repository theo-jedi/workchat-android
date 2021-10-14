package com.theost.workchat.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import com.theost.workchat.R
import com.theost.workchat.databinding.ActivityDialogBinding
import com.theost.workchat.ui.data.InputStatus

class DialogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDialogBinding
    private var inputStatus = InputStatus.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inputLayout.messageInput.addTextChangedListener { onInputTextChanged(it.toString()) }
    }

    private fun onInputTextChanged(text: String) {
        inputStatus = if (text.trim().isNotEmpty()) {
            if (inputStatus == InputStatus.EMPTY)
                binding.inputLayout.actionButton.setImageResource(R.drawable.ic_send)
            InputStatus.FILLED
        } else {
            binding.inputLayout.actionButton.setImageResource(R.drawable.ic_attachment)
            InputStatus.EMPTY
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, DialogActivity::class.java)
        }
    }

}