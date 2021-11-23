package com.theost.workchat.application

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.theost.workchat.database.db.CacheDatabase
import com.theost.workchat.di.GlobalDI
import java.util.concurrent.Executors

class WorkChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalDI.init()

        cacheDatabase = Room.databaseBuilder(
            applicationContext,
            CacheDatabase::class.java,
            "cache_database"
        ).setQueryCallback(
            { sqlQuery, bindArgs ->
                Log.d(
                    "cache_database",
                    "SQL Query: $sqlQuery SQL Args: $bindArgs"
                )
            },
            Executors.newSingleThreadExecutor()
        ).build()
    }

    companion object {
        lateinit var cacheDatabase: CacheDatabase
    }

}