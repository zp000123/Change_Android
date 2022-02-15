package com.zhang.change

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.zhang.change.utils.SharePreferencesUtils

class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        AppHolder.application = this

    }
}