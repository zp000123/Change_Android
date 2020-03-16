package com.zhang.change.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.entitiy.Expend
import com.zhang.change.utils.getNiceStr
import kotlinx.android.synthetic.main.item_expend.view.*


/**
 *  选择员工的适配器
 */
class ExpandAdapter(dataList: MutableList<Expend>, val onDel: (Expend) -> Unit) :
    BaseQuickAdapter<Expend, BaseViewHolder>(R.layout.item_expend, dataList) {
    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: Expend) {
        with(helper.itemView) {
            tv_type.text = item.type.des
            v_expend.text = item.money.getNiceStr()
            v_del.setOnClickListener { onDel(item) }

        }
    }
}