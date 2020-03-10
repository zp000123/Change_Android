package com.zhang.change.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.entitiy.UserBill
import kotlinx.android.synthetic.main.item_bill_no.view.*


/**
 *  选择员工的适配器
 */
class BillAdapter(dataList: MutableList<UserBill>) :
    BaseQuickAdapter<UserBill, BaseViewHolder>(R.layout.item_bill_no, dataList) {
    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: UserBill) {
        with(helper.itemView) {
            tv_no.text = "#${item.no}"
            if (item.income % 100 == 0) {
                tv_income.text = "${item.income / 100}"
            } else {
                tv_income.text = "${item.income / 100.0}"
            }
            if (item.salary % 100 == 0) {
                tv_salary.text = "${item.salary / 100}"
            } else {
                tv_salary.text = "${item.salary / 100.0}"
            }

        }
        addChildClickViewIds(R.id.v_edit, R.id.v_del)
    }

}