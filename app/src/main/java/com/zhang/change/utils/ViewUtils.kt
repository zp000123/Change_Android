package com.zhang.change.utils

import android.widget.TextView
import androidx.annotation.ColorInt
import com.zhang.change.R
import org.jetbrains.anko.textColor

fun TextView.setColorByValue(value: Int) {
    this.textColor = if (value >= 0) context.resources.getColor(com.zhang.change.R.color.black)
    else context.resources.getColor(com.zhang.change.R.color.red)
}

fun TextView.refreshTextColor(position: Int,colorInt: Int = R.color.colorAccent) {
    this.textColor = if (position % 2 == 0) context.resources.getColor(colorInt)
    else context.resources.getColor(R.color.c_6)

}