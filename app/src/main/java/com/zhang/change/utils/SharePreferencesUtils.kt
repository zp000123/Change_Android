package com.zhang.change.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object SharePreferencesUtils {
    fun getCommonSpf(context: Context): SharedPreferences =
        context.getSharedPreferences("Common", Context.MODE_PRIVATE)

    val SHOP_NAME = "shop_name"
}

