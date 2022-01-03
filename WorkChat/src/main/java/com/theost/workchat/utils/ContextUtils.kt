package com.theost.workchat.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat

object ContextUtils {

    fun copyToClipboard(context: Context, label: String, content: String): Boolean {
        val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
        val clip = ClipData.newPlainText(label, content)
        clipboard?.setPrimaryClip(clip)
        return clipboard != null
    }

}