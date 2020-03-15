package com.zhang.change

import android.app.Application
import androidx.room.Room

class MyApplication : Application() {

    lateinit var db: AppDatabase
    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getDatabase(baseContext)
    }
}