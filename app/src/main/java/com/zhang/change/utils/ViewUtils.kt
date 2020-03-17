package com.zhang.change.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import com.zhang.change.R
import org.jetbrains.anko.textColor


fun TextView.setColorByValue(value: Int) {
    this.textColor = if (value >= 0) context.resources.getColor(R.color.black)
    else context.resources.getColor(R.color.red)
}


fun TextView.refreshTextStyle(position: Int) {
    this.textColor = if (position % 2 == 0) context.resources.getColor(R.color.c_6)
    else context.resources.getColor(R.color.black)
}


fun EditText.showKeyboard() {
    val context = this.context
    this.isFocusable = true
    this.isFocusableInTouchMode = true
    //请求获得焦点
    this.requestFocus()
    //调用系统输入法
    val inputManager: InputMethodManager? =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputManager?.showSoftInput(this, 0)
}
