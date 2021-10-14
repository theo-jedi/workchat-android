package com.theost.workchat.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import com.theost.workchat.R
import com.theost.workchat.databinding.ActivityDialogBinding

class DialogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDialogBinding
    private var isInputEmpty = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inputLayout.messageInput.addTextChangedListener { onInputTextChanged(it.toString()) }
    }

    private fun onInputTextChanged(text: String) {
        isInputEmpty = if (text.trim().isNotEmpty()) {
            if (isInputEmpty)
                binding.inputLayout.actionButton.setImageResource(R.drawable.ic_send)
            false
        } else {
            binding.inputLayout.actionButton.setImageResource(R.drawable.ic_attach_file)
            true
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, DialogActivity::class.java)
        }
    }

}