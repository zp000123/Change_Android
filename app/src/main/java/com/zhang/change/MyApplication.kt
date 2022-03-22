package com.zhang.change

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.zhang.change.utils.SharePreferencesUtils

/**
 * 全局的Application , 存储了 数据库等全局资源。
 */
class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        AppHolder.application = this

    }
}