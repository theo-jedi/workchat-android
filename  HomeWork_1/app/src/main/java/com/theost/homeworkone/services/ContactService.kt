package com.theost.homeworkone.services

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.os.IBinder
import android.provider.ContactsContract
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ContactService : Service() {

    override fun onStartCommand(i: Intent, flags: Int, startId: Int): Int {
        Thread {
            val contacts = getContacts()
            val intent = Intent(CONTACTS_ACTION)
            intent.putExtra(CONTACTS_EXTRA, contacts)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }.start()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun getContacts(): String {
        val builder = StringBuilder()
        val resolver: ContentResolver = contentResolver
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        )
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                )).toInt()

                if (phoneNumber > 0) {
                    val cursorPhone = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        arrayOf(id),
                        null
                    )

                    if (cursorPhone != null && cursorPhone.count > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            builder.append(name).append("\n")
                                .append(
                                    phoneNumValue
                                ).append("\n\n")
                        }
                    }
                    cursorPhone?.close()
                }
            }
        } else {
            return ""
        }
        cursor.close()
        return builder.toString()
    }

    companion object {
        const val CONTACTS_EXTRA = "contacts"
        const val CONTACTS_ACTION = "contacts-filter"
    }

}