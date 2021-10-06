package com.theost.homeworkone.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.theost.homeworkone.R
import com.theost.homeworkone.services.ContactService
import com.theost.homeworkone.services.ContactService.Companion.CONTACTS_EXTRA
import com.theost.homeworkone.services.ContactService.Companion.CONTACTS_ACTION

class SecondActivity : AppCompatActivity() {

    private val contactsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val contacts = intent.getStringExtra(CONTACTS_EXTRA)
            if (contacts?.isNotEmpty() == true)
                setResult(RESULT_OK, intent)
            else
                setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            contactsReceiver,
            IntentFilter(CONTACTS_ACTION)
        )

        startContactService()
    }

    private fun startContactService() {
        val intent = Intent(this, ContactService::class.java)
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(contactsReceiver)
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, SecondActivity::class.java)
        }
    }

}