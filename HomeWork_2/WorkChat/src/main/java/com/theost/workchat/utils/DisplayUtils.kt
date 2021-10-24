package com.theost.workchat.utils

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object DisplayUtils {

    fun hideKeyboard(activity: Activity?) {
        activity?.currentFocus?.let { view ->
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun animateArrowExpand(view: View) {
        ObjectAnimator.ofFloat(
            view,
            "rotation",
            0f,
            180f)
            .apply { duration = 300 }
            .start()
    }

    fun animateArrowCollapse(view: View) {
        ObjectAnimator.ofFloat(
            view,
            "rotation",
            180f,
            0f)
            .apply { duration = 300 }
            .start()
    }

}