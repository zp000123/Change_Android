package com.zhang.change.rounter

import android.content.Context
import android.content.Intent
import com.zhang.change.ui.add_performance.AddPerformanceActivity

fun Context.start2Activity(cls: Class<*>) {
    this.startActivity(Intent(this, cls))
}

fun Context.start2AddPerformanceActivity(dateStamp: Long) {
    this.startActivity(Intent(this, AddPerformanceActivity::class.java).apply {
        putExtra(KEY_DATE, dateStamp)
    })
}

const val KEY_DATE = "key_date"