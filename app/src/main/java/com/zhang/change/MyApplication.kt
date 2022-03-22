package com.zhang.change

import android.app.Application
import androidx.room.Room
import com.zhang.change.utils.SharePreferencesUtils

/**
 * 全局的Application , 存储了 数据库等全局资源。
 */
class MyApplication : Application() {

    lateinit var db: AppDatabase
    var shopName: String = ""
    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getDatabase(baseContext)

        val spf = SharePreferencesUtils.getCommonSpf(baseContext)
        shopName = spf.getString(SharePreferencesUtils.SHOP_NAME, "").toString()
    }
}