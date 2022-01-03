package com.theost.workchat.ui.interfaces

import android.view.View
import com.google.android.material.snackbar.Snackbar

interface WindowHolder {
    fun showSnackbar(snackbar: Snackbar, view: View? = null)
    fun hideSnackbar()
}