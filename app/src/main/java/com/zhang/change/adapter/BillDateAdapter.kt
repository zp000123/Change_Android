package com.zhang.change.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.entitiy.UserBill
import com.zhang.change.utils.DateFormat
import com.zhang.change.utils.date2String
import com.zhang.change.utils.getNiceStr
import com.zhang.change.utils.refreshTextStyle
import kotlinx.android.synthetic.main.item_bill_date.view.*


/**
 *  选择员工的适配器
 */
class BillDateAdapter(dataList: MutableList<UserBill>) :
    BaseQuickAdapter<UserBill, BaseViewHolder>(R.layout.item_bill_date, dataList) {

    override fun convert(helper: BaseViewHolder, item: UserBill) {
        with(helper.itemView) {
            tv_date.text = item.dateStamp.date2String(DateFormat.M_D)
            tv_income.text = item.income.getNiceStr()
            tv_salary.text = item.salary.getNiceStr()

            tv_income.refreshTextStyle(helper.adapterPosition)
            tv_salary.refreshTextStyle(helper.adapterPosition)
        }
        addChildClickViewIds(R.id.v_edit)
    }

}
