package com.zhang.change.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.entitiy.User
import kotlinx.android.synthetic.main.item_user.view.*

/**
 *  选择员工的适配器
 */
class UserListAdapter(dataList: MutableList<User>) :
    BaseQuickAdapter<User, BaseViewHolder>(R.layout.item_user, dataList) {
    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: User) {
        with(helper.itemView) {
            v_no.text = "#${item.no}"
        }
    }

}