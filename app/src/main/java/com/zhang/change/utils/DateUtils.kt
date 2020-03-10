package com.zhang.change.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun Long.date2String(format: DateFormat): String {
    val sf = SimpleDateFormat(format.formatStr)
    return sf.format(this)
}

enum class DateFormat(val formatStr: String) {
    YYYY_MM_DD("yyyy-MM-dd"),
    M_D("M-d");
}