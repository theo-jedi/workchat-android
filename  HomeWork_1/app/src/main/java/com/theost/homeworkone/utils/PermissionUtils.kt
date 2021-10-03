package com.theost.homeworkone.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat

object PermissionUtils {
    private const val PERMISSIONS_REQUEST_READ_CONTACTS = 0

    fun checkContactPermission(activity: Activity): Boolean {
        return if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                activity, arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
            false
        } else {
            true
        }
    }
}