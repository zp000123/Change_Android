package com.zhang.change.utils

import android.widget.TextView
import org.jetbrains.anko.textColor

fun TextView.setColorByValue(value: Int) {
    this.textColor = if (value >= 0) context.resources.getColor(com.zhang.change.R.color.black)
    else context.resources.getColor(com.zhang.change.R.color.red)
}