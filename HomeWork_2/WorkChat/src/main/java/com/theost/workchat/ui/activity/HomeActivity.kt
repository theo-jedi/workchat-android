package com.theost.workchat.ui.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.setPadding
import androidx.core.view.size
import com.theost.workchat.R
import com.theost.workchat.databinding.ActivityHomeBinding
import com.theost.workchat.ui.views.ReactionLayout
import com.theost.workchat.ui.views.ReactionView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reactionLayout = binding.message.findViewById<ReactionLayout>(R.id.reactionLayout)
        findViewById<ImageView>(R.id.addEmojiButton).setOnClickListener {
            val reactionView = ReactionView(this).apply {
                emoji = "\uD83D\uDE0B"
                count = 1
                setPadding(28)
            }
            reactionLayout.addView(reactionView, reactionLayout.size)
        }

        binding.dialogActivityButton.setOnClickListener {
            startActivity(DialogActivity.createIntent(this, 0))
        }
    }

}