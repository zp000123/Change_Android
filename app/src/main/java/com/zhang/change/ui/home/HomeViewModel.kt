package com.zhang.change.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zhang.change.AppDatabase
import com.zhang.change.AppHolder
import com.zhang.change.utils.SharePreferencesUtils

class HomeViewModel : ViewModel() {
    val shopName by lazy {
        SharePreferencesUtils.getCommonSpf(AppHolder.application)
            .getString(SharePreferencesUtils.SHOP_NAME, "").toString()
    }


    private val db by lazy { AppDatabase.getDatabase(AppHolder.application) }


    suspend fun hasUser(): Boolean {
        return db.userDao().queryAllUser().isNotEmpty()

    }

    fun setShopName(name: String) {
        SharePreferencesUtils.getCommonSpf(AppHolder.application).edit()
            .putString(SharePreferencesUtils.SHOP_NAME, name).apply()
    }

}