package com.zhang.change.utils

import android.content.Context
import android.content.SharedPreferences

object SharePreferencesUtils {
    fun getCommonSpf(context: Context): SharedPreferences =
        context.getSharedPreferences("Common", Context.MODE_PRIVATE)

    val SHOP_NAME = "shop_name"
}
