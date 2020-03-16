package com.zhang.change.rounter

import android.content.Context
import android.content.Intent

fun Context.start2Activity(cls: Class<*>) {
    this.startActivity(Intent(this,cls))
}
