package com.zhang.change.utils

import android.graphics.Typeface
import android.widget.TextView
import com.zhang.change.R
import org.jetbrains.anko.textColor


fun TextView.setColorByValue(value: Int) {
    this.textColor = if (value >= 0) context.resources.getColor(R.color.black)
    else context.resources.getColor(R.color.red)
}


fun TextView.refreshTextStyle(position: Int) {
    this.textColor = if (position % 2 == 0) context.resources.getColor(R.color.c_6)
    else  context.resources.getColor(R.color.black)
}