package com.theost.workchat.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager


object DisplayUtils {

    fun hideKeyboard(activity: Activity?) {
        activity?.currentFocus?.let { view ->
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}