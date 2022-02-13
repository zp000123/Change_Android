package com.zhang.change.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.databinding.ItemExpendTypeBinding
import com.zhang.change.entitiy.ExpendTypeW


/**
 *  选择员工的适配器
 */
class ExpendTypeAdapter(
    dataList: MutableList<ExpendTypeW>,
    val onSelect: (user: ExpendTypeW) -> Unit
) :
    BaseQuickAdapter<ExpendTypeW, BaseViewHolder>(R.layout.item_expend_type, dataList) {
    private lateinit var binding: ItemExpendTypeBinding
    override fun convert(helper: BaseViewHolder, item: ExpendTypeW) {
        binding = ItemExpendTypeBinding.bind(helper.itemView)

        with(binding.vExpend) {
            text = item.expendType.des
            isChecked = item.u_selected
            setOnClickListener {
                item.u_selected = true
                onSelect(item)
                data.filter { it != item }.forEach { it.u_selected = false }
                notifyDataSetChanged()
            }
        }
    }
}
