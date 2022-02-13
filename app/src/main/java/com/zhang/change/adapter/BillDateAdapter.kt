package com.zhang.change.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.databinding.ItemBillDateBinding
import com.zhang.change.databinding.ItemBillNoBinding
import com.zhang.change.entitiy.UserBill
import com.zhang.change.utils.DateFormat
import com.zhang.change.utils.date2String
import com.zhang.change.utils.getNiceStr
import com.zhang.change.utils.refreshTextStyle


/**
 *  选择员工的适配器
 */
class BillDateAdapter(dataList: MutableList<UserBill>) :
    BaseQuickAdapter<UserBill, BaseViewHolder>(R.layout.item_bill_date, dataList) {
    private lateinit var binding: ItemBillDateBinding
    override fun convert(helper: BaseViewHolder, item: UserBill) {
        binding = ItemBillDateBinding.bind(helper.itemView)
        with( binding ) {
            tvDate.text = item.dateStamp.date2String(DateFormat.M_D)
            tvIncome.text = item.income.getNiceStr()
            tvSalary.text = item.salary.getNiceStr()

            tvIncome.refreshTextStyle(helper.adapterPosition)
            tvSalary.refreshTextStyle(helper.adapterPosition)
        }
        addChildClickViewIds(R.id.v_edit)
    }

}
