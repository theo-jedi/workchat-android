package com.theost.workchat.ui.widgets

interface ToolbarHolder {
    fun setToolbarTitle(title : String)
    fun setToolbarNavigationIcon(resourceId : Int)
    fun hideToolbar()
    fun showToolbar()
}