package com.zhang.change.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.databinding.ItemBillNoBinding
import com.zhang.change.entitiy.UserBill
import com.zhang.change.utils.getNiceStr
import com.zhang.change.utils.refreshTextStyle


/**
 *  选择员工的适配器
 */
class BillAdapter(dataList: MutableList<UserBill>, val onDel: (UserBill) -> Unit) :
    BaseQuickAdapter<UserBill, BaseViewHolder>(R.layout.item_bill_no, dataList) {
    private lateinit var binding:ItemBillNoBinding

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: UserBill) {
        binding = ItemBillNoBinding.bind(helper.itemView)
        with(binding) {
            tvNo.text = "#${item.no}"
            tvIncome.text = item.income.getNiceStr()
            tvSalary.text = item.salary.getNiceStr()
            vDel.setOnClickListener { onDel(item) }

            tvIncome.refreshTextStyle(helper.adapterPosition)
            tvSalary.refreshTextStyle(helper.adapterPosition)
        }

    }

}