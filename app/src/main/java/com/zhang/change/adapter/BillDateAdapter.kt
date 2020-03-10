package com.zhang.change.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.entitiy.UserBill
import com.zhang.change.utils.DateFormat
import com.zhang.change.utils.date2String
import kotlinx.android.synthetic.main.item_bill_date.view.*


/**
 *  选择员工的适配器
 */
class BillDateAdapter(dataList: MutableList<UserBill>) :
    BaseQuickAdapter<UserBill, BaseViewHolder>(R.layout.item_bill_date, dataList) {
    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: UserBill) {
        with(helper.itemView) {
            tv_date.text = item.dateStamp.date2String(DateFormat.M_D)
            tv_income.text = item.income.getNicePenStr()
            tv_salary.text = item.salary.getNicePenStr()

        }
        addChildClickViewIds(R.id.v_edit, R.id.v_del)
    }

}

fun Int.getNicePenStr(): String {
    return if (this % 100 == 0) {
        "${this / 100}"
    } else {
        "${this / 100.0}"
    }
}