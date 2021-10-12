package com.theost.workchat.ui.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.setPadding
import androidx.core.view.size
import com.theost.workchat.R
import com.theost.workchat.databinding.ActivityHomeBinding
import com.theost.workchat.ui.views.EmojiLayout
import com.theost.workchat.ui.views.EmojiView

private lateinit var binding: ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emojiLayout = binding.message.findViewById<EmojiLayout>(R.id.emojiLayout)
        findViewById<ImageView>(R.id.addButton).setOnClickListener {
            val emojiView = EmojiView(this).apply {
                emoji = "\uD83D\uDE0B"
                count = 1
                setPadding(28)
            }
            emojiLayout.addView(emojiView, emojiLayout.size)
        }
    }

}