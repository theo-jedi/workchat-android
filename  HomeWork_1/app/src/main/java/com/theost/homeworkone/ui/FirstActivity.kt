package com.theost.homeworkone.ui

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.theost.homeworkone.R
import com.theost.homeworkone.databinding.ActivityFirstBinding
import com.theost.homeworkone.services.ContactService.Companion.CONTACTS_EXTRA

class FirstActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstBinding
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            startSecondActivityForResult()
        } else {
            Toast.makeText(this, getString(R.string.error_permission), Toast.LENGTH_SHORT).show()
        }
    }
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
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun startSecondActivityForResult() {
        val intent = SecondActivity.createIntent(this)
        resultLauncher.launch(intent)
    }
}