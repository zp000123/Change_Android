package com.zhang.change

import android.app.Application
import androidx.room.Room
import com.zhang.change.utils.SharePreferencesUtils

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