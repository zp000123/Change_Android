package com.zhang.change.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.zhang.change.R


fun TextView.setColorByValue(value: Int) {
    this.setTextColor(
        if (value >= 0) context.resources.getColor(R.color.black)
        else context.resources.getColor(R.color.red)
    )
}


fun TextView.refreshTextStyle(position: Int) {
    this.setTextColor(
        if (position % 2 == 0) context.resources.getColor(R.color.c_6)
        else context.resources.getColor(R.color.black)
    )
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

fun Activity.toast(str:String){
    Toast.makeText(baseContext,str,Toast.LENGTH_SHORT).show()
}
