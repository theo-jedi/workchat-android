package com.theost.homeworkone.ui

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.theost.homeworkone.R
import com.theost.homeworkone.databinding.ActivityFirstBinding
import com.theost.homeworkone.services.ContactService.Companion.CONTACTS_EXTRA
import com.theost.homeworkone.utils.PermissionUtils

class FirstActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstBinding
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val contacts = result.data!!.getStringExtra(CONTACTS_EXTRA)
                binding.dataText.text = contacts
            } else {
                binding.dataText.text = getString(R.string.error_data)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loadButton.setOnClickListener {
            if (PermissionUtils.checkContactPermission(this)) {
                startSecondActivityForResult()
            }
        }
    }

    private fun startSecondActivityForResult() {
        val intent = SecondActivity.createIntent(this)
        resultLauncher.launch(intent)
    }
}