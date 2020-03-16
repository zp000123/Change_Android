package com.zhang.change.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.entitiy.UserBill
import com.zhang.change.utils.getNiceStr
import com.zhang.change.utils.refreshTextColor
import kotlinx.android.synthetic.main.item_bill_no.view.*



/**
 *  选择员工的适配器
 */
class BillAdapter(dataList: MutableList<UserBill>, val onDel: (UserBill) -> Unit) :
    BaseQuickAdapter<UserBill, BaseViewHolder>(R.layout.item_bill_no, dataList) {
    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: UserBill) {
        with(helper.itemView) {
            tv_no.text = "#${item.no}"
            tv_income.text = item.income.getNiceStr()
            tv_salary.text = item.salary.getNiceStr()
            v_del.setOnClickListener { onDel(item) }

            tv_income.refreshTextColor(helper.adapterPosition,R.color.yColorPrimary)
            tv_salary.refreshTextColor(helper.adapterPosition,R.color.yColorPrimary)
        }

    }

}