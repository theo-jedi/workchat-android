package com.theost.workchat.application

import android.app.Application
import com.theost.workchat.di.base.AppComponent
import com.theost.workchat.di.base.DaggerAppComponent

class WorkChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(applicationContext)
    }

    companion object {
        lateinit var appComponent: AppComponent
    }

}